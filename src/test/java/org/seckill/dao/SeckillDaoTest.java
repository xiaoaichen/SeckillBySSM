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

//����junit��spring����,junit����ʱ����springIOC����
@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�����ļ�
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
		 * Seckill [seckillId=1000, name=200Ԫ��ɱ�·�, number=20, startTime=Wed Oct 09 20:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		 */
	
	@Test
	public void testQueryAll() throws Exception{
		List<Seckill> seckills = seckillDao.queryAll(0, 100);
		for(Seckill seckill : seckills) {
			System.out.println(seckill);
		}
	}
	/*
	 * Seckill [seckillId=1000, name=200Ԫ��ɱ�·�, number=20, startTime=Wed Oct 09 20:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1001, name=500Ԫ��ɱ���˻�, number=10, startTime=Wed Oct 09 09:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1002, name=100Ԫ��ɱ�ֱ�, number=10, startTime=Thu Oct 10 10:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1003, name=500Ԫ��ɱ�˶�Ь, number=50, startTime=Thu Oct 10 11:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1004, name=1000Ԫ��ɱ�ֻ�, number=20, startTime=Thu Oct 10 12:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1005, name=40Ԫ��ɱƻ��, number=20, startTime=Wed Oct 09 04:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1006, name=10Ԫ��ɱ�㽶, number=20, startTime=Wed Oct 09 05:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
		Seckill [seckillId=1007, name=20Ԫ��ɱ����, number=20, startTime=Wed Oct 09 06:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
	 */
	
	@Test
	public void testReduceNumber() throws Exception{
		Date killTime = new Date();
		int updateCount = seckillDao.reduceNumber(1001L, killTime);
		System.out.println("updateCount="+updateCount);
		//updateCount<=0 ��ʾû�и��µ���¼����ɱ����
	}
	/*
	 * 13:57:14.304 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==>  Preparing: update seckill set number = number - 1 where seckill_id = ? and start_time <= ? and end_time >= ? and number > 0; 
		13:57:14.307 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==> Parameters: 1000(Long), 2019-09-18 13:57:14.303(Timestamp), 2019-09-18 13:57:14.303(Timestamp)
		13:57:14.316 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - <==    Updates: 1
	 	updateCount=1
	 */
}
