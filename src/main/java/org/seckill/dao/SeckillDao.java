package org.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

public interface SeckillDao {

	/**
	 * �����
	 * @param("seckillId")��������Ϊ�˸���mybatis��ȷ���β���ʲô����Ȼ�����ʵ���ֻ��arg0
	 * @param seckillId
	 * @param killTime
	 * @return
	 */
	int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime")Date killTime);

	/**
	 * ����id��ѯ��ɱ����
	 * 
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);

	/**
	 * ����ƫ������ѯ��ɱ��Ʒ
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);

	/**
	 * ʹ�ô洢����ִ����ɱ
	 * @param paraMap
	 */
	void killByProcedure(Map<String,Object> paraMap);
}
