package org.seckill.exception;

/*
 * ��ɱ�ر��쳣
 */
public class SeckillCloseException extends SeckillException{
	public SeckillCloseException() {
		super();
	}

	public SeckillCloseException(String message) {
		super(message);
	}

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}
}
