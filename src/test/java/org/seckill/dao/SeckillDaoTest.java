package org.seckill.dao;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.annotation.Resource;

//配置junit和spring整合,junit启动时加载springIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

	@Resource
	private SeckillDao seckillDao;
	
	@Test
	public void testQueryById() throws Exception{
		long id = 1000;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill);
	}
		/*
		 * Seckill [seckillId=1000, name=200元秒杀衣服, number=20, startTime=Wed Oct 09 20:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		 */
	
	@Test
	public void testQueryAll() throws Exception{
		List<Seckill> seckills = seckillDao.queryAll(0, 100);
		for(Seckill seckill : seckills) {
			System.out.println(seckill);
		}
	}
	/*
	 * Seckill [seckillId=1000, name=200元秒杀衣服, number=20, startTime=Wed Oct 09 20:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1001, name=500元秒杀无人机, number=10, startTime=Wed Oct 09 09:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1002, name=100元秒杀钢笔, number=10, startTime=Thu Oct 10 10:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1003, name=500元秒杀运动鞋, number=50, startTime=Thu Oct 10 11:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1004, name=1000元秒杀手机, number=20, startTime=Thu Oct 10 12:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1005, name=40元秒杀苹果, number=20, startTime=Wed Oct 09 04:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1006, name=10元秒杀香蕉, number=20, startTime=Wed Oct 09 05:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1007, name=20元秒杀葡萄, number=20, startTime=Wed Oct 09 06:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
	 */
	
	@Test
	public void testReduceNumber() throws Exception{
		Date killTime = new Date();
		int updateCount = seckillDao.reduceNumber(1001L, killTime);
		System.out.println("updateCount="+updateCount);
		//updateCount<=0 表示没有更新到记录或秒杀结束
	}
	/*
	 * 13:57:14.304 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==>  Preparing: update seckill set number = number - 1 where seckill_id = ? and start_time <= ? and end_time >= ? and number > 0; 
		13:57:14.307 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==> Parameters: 1000(Long), 2019-09-18 13:57:14.303(Timestamp), 2019-09-18 13:57:14.303(Timestamp)
		13:57:14.316 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - <==    Updates: 1
	 	updateCount=1
	 */
}
