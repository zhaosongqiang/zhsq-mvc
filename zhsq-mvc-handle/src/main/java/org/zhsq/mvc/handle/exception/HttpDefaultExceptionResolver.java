package org.zhsq.mvc.handle.exception;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpDefaultExceptionResolver implements ExceptionResolver {

	@Override
	public void dealException(FullHttpRequest request, FullHttpResponse response) {
		response.setStatus(HttpResponseStatus.BAD_REQUEST);
	}

}
