package org.zhsq.mvc.example.intercepter;

import org.zhsq.mvc.handle.intercepter.HttpIntercepter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 *  测试拦截器
 * @author zhsq 
 * @date 2018年5月13日
 * @since 1.0
 */
public class TestIntercepter implements HttpIntercepter {

	@Override
	public boolean intercept(FullHttpRequest request, FullHttpResponse response) {
		System.out.println("org.zhsq.mvc.web.intercepter.HttpIntercepter");
		return true;
	}

}
