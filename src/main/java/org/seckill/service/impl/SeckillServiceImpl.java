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
	// ע��Service����
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
		//redis�Ż����ݿ����
		//redis�����Ż���¶��ַ�ӿڣ��ڳ�ʱ�Ļ�����ά��һ����
		//�ٷ����ͣ�Redis����ν�ļ�ֵ�洢��ͨ����ΪNoSQL���ݿ⡣��ֵ�洢�ı������ڼ��ڲ��洢һЩ���ݣ���Ϊֵ������������������֪�����ڴ洢���ݵ�ȷ����Կʱ���Ժ���ܼ��������ݡ�
		//1.����redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			//2.�������ݿ�
			seckill = seckillDao.queryById(seckillId);
			if (seckill == null) {
				//��ʾ��ɱ��������
				return new Exposer(false, seckillId);
			} else {
				//3.����redis
				redisDao.putSeckill(seckill);
			}
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//ϵͳ��ǰʱ��
		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		//ת���ض��ַ����Ĺ��̣�������
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Transactional
	// ����Ҫ�ķ���
	public SeckillExecution excuteSeckill(long seckillId, long userPhone, String md5)
			throws SeckillCloseException, RepeatKillException, SeckillException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		Date nowTime = new Date();
		//��������߼�˳�򣬽���mybatis��rowlock�м����ĳ���ʱ�䣨�漰�����ӳٺ�GC�����ﵽ�򵥵Ĳ����Ż�
		try {
			//��¼������Ϊ��seckillId��userPhone����Ψһ��
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if (insertCount <= 0) {
				//�ظ���ɱ
				throw new RepeatKillException("seckill repeated");
			} else {
				//�����
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					//û�и��µ���¼����ɱ����
					throw new SeckillCloseException("seckill is closed");
				} else {
					//��ɱ�ɹ�
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

	//���ô洢����
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5)
			throws SeckillCloseException, RepeatKillException, SeckillException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
		}
		//��ǰ��ɱʱ��
		Date killTime = new Date();
		//Map�Ǽ�ֵ�Լ��ϣ��������ظ���HashMap�����ܸ��ã���HashTable���̰߳�ȫ��ʵ����Map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//�洢����ִ�����result����ֵ
		try {
			seckillDao.killByProcedure(map);
			//�쳣ͨ������ȡresult
			int result = MapUtils.getInteger(map, "result", -2);
			if (result == 1) {
				//��ɱ�ɹ�
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, sk);
			} else {
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			//�ڲ��쳣����
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
	}
}
