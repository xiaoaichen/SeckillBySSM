package org.seckill.dao.cache;

import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

//放数据库和访问的类
public class RedisDao {

	//生成一个logger日志对象，slf4j
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	//类似于数据库连接池的connectionPool
	private final JedisPool jedisPool;

	//构造方法，连接jedis的ip和port
	public RedisDao(String ip, int port) {
		//初始化jedis，传入ip和port
		//这个位置需要在spring-dao.xml中传入
		jedisPool = new JedisPool(ip, port);
	}
	//全局定义一个运行期，protostuff动态实现schema，通过获取到seckill的字节码对应的属性/方法，动态序列化
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

	//拿到seckill对象
	public Seckill getSeckill(long seckillId) {
		// redis操作逻辑
		try {
			//拿到jedis对象
			Jedis jedis = jedisPool.getResource();
			try {
				//键值
				String key = "seckill:" + seckillId;
				//并没有实现内部序列化操作
				// get→byte[]→反序列化→Object(Seckill)
				// 自定义序列化，protostuff序列化高效，存的是对象所以使用字节数组
				byte[] bytes = jedis.get(key.getBytes());
				//缓存获取到了
				if (bytes != null) {
					// 空对象
					Seckill seckill = schema.newMessage();
					//节省CPU，压缩空间，提高压缩速度
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					// seckill被反序列化
					return seckill;
				}
			} finally {
				//类似数据库连接池，需要关闭
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	//当发现缓存没有时，put一个seckill
	public String putSeckill(Seckill seckill) {
		// put Object(Seckill)→序列化→byte[]
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				//缓存器LinkeddBuffer
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				// 缓存有效期
				int timeout = 60 * 60;// 1小时
				//超时缓存
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
