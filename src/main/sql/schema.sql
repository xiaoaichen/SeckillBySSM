-- �������ݿ�
CREATE DATABASE seckill;

-- ʹ�����ݿ�
use seckill;

-- ������ɱ����ע���������``,����''
CREATE TABLE seckill(
	`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '��Ʒ���id',
	`name` varchar(120) NOT NULL COMMENT '��Ʒ����',
	`number` int NOT NULL COMMENT '�������',
	`start_time` timestamp NOT NULL COMMENT '��ɱ��ʼʱ��',
	`end_time` timestamp NOT NULL COMMENT '��ɱ����ʱ��',
	`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��',
	PRIMARY KEY(seckill_id),
	KEY idx_start_time(start_time),
	KEY idx_end_time(end_time),
	KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=UTF8 COMMENT='��ɱ����';

-- ��ʼ������
INSERT INTO 
	seckill(name,number,start_time,end_time)
VALUES
	('200Ԫ��ɱ�·�',20,'2019-10-09 12:00:00','2019-10-11 00:00:00'),
	('500Ԫ��ɱ���˻�',10,'2019-10-09 01:00:00','2019-10-11 00:00:00'),
	('100Ԫ��ɱ�ֱ�',10,'2019-10-10 02:00:00','2019-10-11 00:00:00'),
	('500Ԫ��ɱ�˶�Ь',50,'2019-10-10 03:00:00','2019-10-11 00:00:00'),
	('1000Ԫ��ɱ�ֻ�',20,'2019-10-10 04:00:00','2019-10-11 00:00:00'),
    ('40Ԫ��ɱƻ��',20,'2019-10-8 20:00:00','2019-10-11 00:00:00'),
    ('10Ԫ��ɱ�㽶',20,'2019-10-8 21:00:00','2019-10-11 00:00:00'),
    ('20Ԫ��ɱ����',20,'2019-10-8 22:00:00','2019-10-11 00:00:00');
	
-- �û���¼��֤�����Ϣ
CREATE TABLE success_killed(
	`seckill_id` bigint NOT NULL COMMENT '��ɱ��ƷID',
	`user_phone` bigint NOT NULL COMMENT '�û��ֻ���',
	`state` tinyint NOT NULL DEFAULT -1 COMMENT '״̬��ʾ��-1��Ч��0�ɹ���1�Ѹ��2�ѷ���',
	`create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��',
	PRIMARY KEY(seckill_id,user_phone),
	KEY idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='��ɱ�ɹ���ϸ��'; 