package org.zhsq.mvc.handle.filter;

import org.springframework.core.Ordered;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author zhaosq
 * @date 2018年5月11日
 * @since 1.0
 */
public interface HttpFilter extends Ordered {

	/**
	 * 执行过滤
	 * @param request 请求信息
	 * @param response 响应信息
	 */
	void doFilter (FullHttpRequest request, FullHttpResponse response);	

}
