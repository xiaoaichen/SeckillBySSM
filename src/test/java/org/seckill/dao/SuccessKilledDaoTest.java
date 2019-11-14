package org.seckill.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.annotation.Resource;

//����junit��spring����,junit����ʱ����springIOC����
@RunWith(SpringJUnit4ClassRunner.class)
//����junit spring�����ļ�
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

	//����ע�룬DAOʵ��������
	@Resource
	private SuccessKilledDao successKilledDao;
	
	@Test
	public void testInsertSuccessKilled() throws Exception{
		long id = 1000L;
		long phone = 18875161212L;
		int insertCount = successKilledDao.insertSuccessKilled(id,phone);
		System.out.println("insertCount="+insertCount);
	}
	/*
	 * 	��һ��ִ�б�ʾ����ɹ�
		insertCount=1
		
		��ִ��һ�α�ʾʧ��
		insertCount=0
	 */

	@Test
	public void testQueryByIdWithSeckill() throws Exception{
		long id = 1000L;
		long phone = 18875161212L;
		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
		System.out.println(successKilled);
		//System.out.println(successKilled.getSeckill());
	}
	/*
	 * SuccessKilled [seckillId=1009, userPhone=18875161212, state=0, createTime=Sat Jan 01 08:00:00 CST 1]
	 */
}
