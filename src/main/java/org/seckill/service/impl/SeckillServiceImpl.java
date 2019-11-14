package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.nums.SeckillStatEnum;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class SeckillServiceImpl implements SeckillService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	// 注入Service依赖
	@Autowired
	private SeckillDao seckillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;
	@Autowired
	private RedisDao redisDao;

	private final String slat = "n4651515dakjsjn13233^&%@&^";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 100);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		//redis优化数据库操作
		//redis缓存优化暴露地址接口，在超时的基础上维护一致性
		//官方解释：Redis是所谓的键值存储，通常称为NoSQL数据库。键值存储的本质是在键内部存储一些数据（称为值）的能力。仅当我们知道用于存储数据的确切密钥时，以后才能检索该数据。
		//1.访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			//2.访问数据库
			seckill = seckillDao.queryById(seckillId);
			if (seckill == null) {
				//表示秒杀单不存在
				return new Exposer(false, seckillId);
			} else {
				//3.放入redis
				redisDao.putSeckill(seckill);
			}
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//系统当前时间
		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		//转化特定字符串的过程，不可逆
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Transactional
	// 最重要的方法
	public SeckillExecution excuteSeckill(long seckillId, long userPhone, String md5)
			throws SeckillCloseException, RepeatKillException, SeckillException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		Date nowTime = new Date();
		//调整后的逻辑顺序，降低mybatis的rowlock行级锁的持有时间（涉及网络延迟和GC），达到简单的并发优化
		try {
			//记录购买行为，seckillId和userPhone具有唯一性
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if (insertCount <= 0) {
				//重复秒杀
				throw new RepeatKillException("seckill repeated");
			} else {
				//减库存
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					//没有更新到记录，秒杀结束
					throw new SeckillCloseException("seckill is closed");
				} else {
					//秒杀成功
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillCloseException e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SeckillException("secckill inner error:" + e.getMessage());
		}
	}

	public SeckillExecution executeSeckill(long seckill, long usePhone, String md5)
			throws SeckillCloseException, RepeatKillException, SeckillException {
		return null;
	}

	//调用存储过程
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5)
			throws SeckillCloseException, RepeatKillException, SeckillException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
		}
		//当前秒杀时间
		Date killTime = new Date();
		//Map是键值对集合，键不可重复，HashMap（性能更好）和HashTable（线程安全）实现于Map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//存储过程执行完后，result被赋值
		try {
			seckillDao.killByProcedure(map);
			//异常通过，获取result
			int result = MapUtils.getInteger(map, "result", -2);
			if (result == 1) {
				//秒杀成功
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, sk);
			} else {
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			//内部异常处理
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
	}
}
