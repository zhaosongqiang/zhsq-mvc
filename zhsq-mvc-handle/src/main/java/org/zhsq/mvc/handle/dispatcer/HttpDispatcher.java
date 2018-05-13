package org.zhsq.mvc.handle.dispatcer;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author zhaosq
 * @date 2018年5月13日
 * @since 1.0
 */
public interface HttpDispatcher {

	/**
	 * 根据请求信息通过handlerMapping分派请求到controller
	 * @param request 请求信息
	 * @param response 响应信息
	 */
	void doDispatcher (FullHttpRequest request, FullHttpResponse response);

}
