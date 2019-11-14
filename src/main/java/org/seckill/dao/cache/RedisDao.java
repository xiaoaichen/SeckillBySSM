package org.seckill.dao.cache;

import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

//�����ݿ�ͷ��ʵ���
public class RedisDao {

	//����һ��logger��־����slf4j
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	//���������ݿ����ӳص�connectionPool
	private final JedisPool jedisPool;

	//���췽��������jedis��ip��port
	public RedisDao(String ip, int port) {
		//��ʼ��jedis������ip��port
		//���λ����Ҫ��spring-dao.xml�д���
		jedisPool = new JedisPool(ip, port);
	}
	//ȫ�ֶ���һ�������ڣ�protostuff��̬ʵ��schema��ͨ����ȡ��seckill���ֽ����Ӧ������/��������̬���л�
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

	//�õ�seckill����
	public Seckill getSeckill(long seckillId) {
		// redis�����߼�
		try {
			//�õ�jedis����
			Jedis jedis = jedisPool.getResource();
			try {
				//��ֵ
				String key = "seckill:" + seckillId;
				//��û��ʵ���ڲ����л�����
				// get��byte[]�������л���Object(Seckill)
				// �Զ������л���protostuff���л���Ч������Ƕ�������ʹ���ֽ�����
				byte[] bytes = jedis.get(key.getBytes());
				//�����ȡ����
				if (bytes != null) {
					// �ն���
					Seckill seckill = schema.newMessage();
					//��ʡCPU��ѹ���ռ䣬���ѹ���ٶ�
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					// seckill�������л�
					return seckill;
				}
			} finally {
				//�������ݿ����ӳأ���Ҫ�ر�
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	//�����ֻ���û��ʱ��putһ��seckill
	public String putSeckill(Seckill seckill) {
		// put Object(Seckill)�����л���byte[]
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				//������LinkeddBuffer
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				// ������Ч��
				int timeout = 60 * 60;// 1Сʱ
				//��ʱ����
				String result = jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
