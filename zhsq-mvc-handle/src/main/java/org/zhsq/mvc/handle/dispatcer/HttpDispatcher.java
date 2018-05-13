package org.zhsq.mvc.handle.dispatcer;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author zhaosq
 * @date 2018年5月13日
 * @since 1.0
 */
public interface HttpDispatcher {

	void doDispatcher (FullHttpRequest request, FullHttpResponse response);

}
