package org.seckill.exception;

/*
 * ������ɱҵ���쳣
 */
public class SeckillException extends RuntimeException{
	public SeckillException() {
		super();
	}

	public SeckillException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeckillException(String message) {
		super(message);
	}
}
