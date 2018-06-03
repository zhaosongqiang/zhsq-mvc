package org.zhsq.mvc.handle.argumentresolver;

import java.lang.reflect.Method;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author zhaosq
 * @date 2018年5月19日
 * @since 1.0
 */
public interface ArgResolver {

	/**
	 * 根据不同的 Content-Type 解析参数
	 * @param request 请求信息
	 * @param handlerMethod 被解析的方法
	 * @return 解析后的参数
	 */
	Object[] doResolve(FullHttpRequest request, Method handlerMethod);

}
