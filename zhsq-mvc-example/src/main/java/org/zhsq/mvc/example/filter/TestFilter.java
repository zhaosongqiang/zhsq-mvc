package org.zhsq.mvc.example.filter;

import org.zhsq.mvc.handle.filter.HttpFilter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

/**
 * 测试Filter
 * @author zhsq 
 * @date 2018年5月13日
 * @since 1.0
 */
public class TestFilter implements HttpFilter {

	@Override
	public void doFilter(FullHttpRequest request, FullHttpResponse response) {
		System.out.println("org.zhsq.mvc.web.filter.HttpFilter");
		response.headers().set("Content-Type", "text/html;charset=UTF-8");
		StringBuilder buf = new StringBuilder("hello:zsq333");
		ByteBuf buffer = Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
		response.content().writeBytes(buffer);
		buffer.release();
	}

}
