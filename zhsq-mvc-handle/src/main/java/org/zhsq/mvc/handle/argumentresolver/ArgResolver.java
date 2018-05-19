package org.zhsq.mvc.handle.argumentresolver;

import java.lang.reflect.Method;

import io.netty.handler.codec.http.FullHttpRequest;

public interface ArgResolver {

	/**
	 * 根据不同的 Content-Type 解析参数
	 * @param request 请求信息
	 * @return 解析后的参数
	 */
	Object[] doResolve(FullHttpRequest request, Method handlerMethod);

}
