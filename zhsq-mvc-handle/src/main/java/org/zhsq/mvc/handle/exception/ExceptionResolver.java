package org.zhsq.mvc.handle.exception;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface ExceptionResolver {
	
	
	void dealException(FullHttpRequest request, FullHttpResponse response);

}
