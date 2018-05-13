package org.zhsq.mvc.handle.filter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author zhaosq
 * @date 2018年5月11日
 * @since 1.0
 */
public interface HttpFilter {

	void doFilter (FullHttpRequest request, FullHttpResponse response);	

}
