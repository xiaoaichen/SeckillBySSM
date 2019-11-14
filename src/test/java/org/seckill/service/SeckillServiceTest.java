package org.seckill.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml" })
public class SeckillServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@Test
	public void testGetSeckillList() throws Exception {
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list={}", list);
	}
	/*
	 * 14:12:47.002 [main] INFO o.seckill.service.SeckillServiceTest - list=
	 * [Seckill [seckillId=1004, name=1000Ԫ��ɱ�ֻ�, number=20, startTime=Wed Sep 18
	 * 08:00:00 CST 2019, endTime=Thu Sep 19 08:00:00 CST 2019, createTime=Wed Sep
	 * 18 09:35:22 CST 2019], Seckill [seckillId=1003, name=500Ԫ��ɱ�˶�Ь, number=50,
	 * startTime=Wed Sep 18 08:00:00 CST 2019, endTime=Thu Sep 19 08:00:00 CST 2019,
	 * createTime=Wed Sep 18 09:35:22 CST 2019], Seckill [seckillId=1002,
	 * name=100Ԫ��ɱ�ֱ�, number=10, startTime=Wed Sep 18 08:00:00 CST 2019, endTime=Thu
	 * Sep 19 08:00:00 CST 2019, createTime=Wed Sep 18 09:35:22 CST 2019], Seckill
	 * [seckillId=1001, name=500Ԫ��ɱ���˻�, number=10, startTime=Wed Sep 18 08:00:00 CST
	 * 2019, endTime=Thu Sep 19 08:00:00 CST 2019, createTime=Wed Sep 18 09:35:22
	 * CST 2019]]
	 */

	@Test
	public void testGetById() throws Exception {
		long id = 1000;
		Seckill seckill = seckillService.getById(id);
		logger.info("seckill={}", seckill);
	}
	/*
	 * seckill=Seckill [seckillId=1007, name=20Ԫ��ɱ����, number=20, startTime=Wed Oct 09 06:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 03:18:34 CST 2019]
	 */

	@Test
	public void testSeckillLogic() throws Exception {
		long id = 1000;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if (exposer.isExposed()) {
			System.out.println("����");
			long phone = 12736367373L;
			String md5 = exposer.getMd5();
			System.out.println("md5="+md5);
			try {
				SeckillExecution execution = seckillService.executeSeckillProcedure(id, phone, md5);
				logger.info("result={}",execution);
			} catch (RepeatKillException e) {
				logger.error(e.getMessage());
			} catch (SeckillCloseException e) {
				logger.error(e.getMessage());
			}
		} else {
			// ��ɱδ����
			logger.warn("exposer={}", exposer);
			System.out.println("δ����");
		}
	}

	public void executeSeckillProcedure() {
		long seckillId = 1000;
		long phone = 18875161212L;
		//�����ɱ��¶��ַ
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()) {
			String md5 = exposer.getMd5();
			//ͨ���洢����ִ����ɱ
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
			//��ȡ���
			logger.info(execution.getStateInfo());
		}
	}
}
