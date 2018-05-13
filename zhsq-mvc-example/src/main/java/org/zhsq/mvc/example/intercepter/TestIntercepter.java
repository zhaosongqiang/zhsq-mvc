package org.zhsq.mvc.example.intercepter;

import org.zhsq.mvc.handle.intercepter.HttpIntercepter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class TestIntercepter implements HttpIntercepter {

	@Override
	public boolean intercept(FullHttpRequest request, FullHttpResponse response) {
		System.out.println("org.zhsq.mvc.web.intercepter.HttpIntercepter");
		return true;
	}

}
