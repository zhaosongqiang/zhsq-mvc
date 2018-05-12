/**
 * 
 */
package org.zhsq.mvc.transport.server.exception;

/**
 * 服务启动异常
 * @author zhaosq
 * @date 2018年5月12日
 * @since 1.0
 */
public class ServerBindException extends RuntimeException {

	private static final long serialVersionUID = -3038379449152601310L;

	public ServerBindException (String message) {
		super(message);
	}

	public ServerBindException (String message, Exception e) {
		super(message, e);
	}

}
