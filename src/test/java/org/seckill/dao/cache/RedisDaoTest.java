package org.seckill.dao.cache;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class RedisDaoTest {

    private long id = 1001;

    //spring����ע��
    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() {
        //����get and put
        Seckill seckill = redisDao.getSeckill(id);
        if (seckill == null) {
            //�����ݿ�ȡ
        	seckill = seckillDao.queryById(id);
            if (seckill != null) {
            	//���ݶ���
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                //��һ�����
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }
    /*
     * OK
		Seckill [seckillId=1001, name=500Ԫ��ɱ���˻�, number=10, startTime=Mon Sep 23 08:00:00 CST 2019, endTime=Wed Sep 25 08:00:00 CST 2019, createTime=Mon Sep 23 22:43:04 CST 2019]
		���� 27, 2019 10:37:12 ���� org.springframework.context.support.GenericApplicationContext doClose
     */
}
