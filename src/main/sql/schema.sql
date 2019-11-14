-- 创建数据库
CREATE DATABASE seckill;

-- 使用数据库
use seckill;

-- 创建秒杀库存表，注意这个符号``,不是''
CREATE TABLE seckill(
	`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
	`name` varchar(120) NOT NULL COMMENT '商品名称',
	`number` int NOT NULL COMMENT '库存数量',
	`start_time` timestamp NOT NULL COMMENT '秒杀开始时间',
	`end_time` timestamp NOT NULL COMMENT '秒杀结束时间',
	`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY(seckill_id),
	KEY idx_start_time(start_time),
	KEY idx_end_time(end_time),
	KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=UTF8 COMMENT='秒杀库存表';

-- 初始化数据
INSERT INTO 
	seckill(name,number,start_time,end_time)
VALUES
	('200元秒杀衣服',20,'2019-10-09 12:00:00','2019-10-11 00:00:00'),
	('500元秒杀无人机',10,'2019-10-09 01:00:00','2019-10-11 00:00:00'),
	('100元秒杀钢笔',10,'2019-10-10 02:00:00','2019-10-11 00:00:00'),
	('500元秒杀运动鞋',50,'2019-10-10 03:00:00','2019-10-11 00:00:00'),
	('1000元秒杀手机',20,'2019-10-10 04:00:00','2019-10-11 00:00:00'),
    ('40元秒杀苹果',20,'2019-10-8 20:00:00','2019-10-11 00:00:00'),
    ('10元秒杀香蕉',20,'2019-10-8 21:00:00','2019-10-11 00:00:00'),
    ('20元秒杀葡萄',20,'2019-10-8 22:00:00','2019-10-11 00:00:00');
	
-- 用户登录认证相关信息
CREATE TABLE success_killed(
	`seckill_id` bigint NOT NULL COMMENT '秒杀商品ID',
	`user_phone` bigint NOT NULL COMMENT '用户手机号',
	`state` tinyint NOT NULL DEFAULT -1 COMMENT '状态表示：-1无效，0成功，1已付款，2已发货',
	`create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY(seckill_id,user_phone),
	KEY idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='秒杀成功明细表'; 