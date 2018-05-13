package org.zhsq.mvc.handle.intercepter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * HTTP协议请求处理的拦截器
 * @author zhaosq
 * @date 2018年5月11日
 * @since 1.0
 */
public interface HttpIntercepter {

	/**
	 * 拦截请求
	 * @param request HTTP请求信息
	 * @param response HTTP响应信息
	 * @param next 下一个拦截器
	 * @return 拦截是否通过,true：通过拦截调用进入Filter ;false：未通过拦截调用通过response返回信息
	 */
	boolean intercept (FullHttpRequest request, FullHttpResponse response);

}
