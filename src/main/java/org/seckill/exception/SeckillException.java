package org.seckill.exception;

/*
 * 所有秒杀业务异常
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
