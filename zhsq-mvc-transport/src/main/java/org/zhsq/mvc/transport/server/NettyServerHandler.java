package org.zhsq.mvc.transport.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * @author zhaosq
 * @date 2018年5月11日
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		String uri = request.uri();
		
		//TODO 这里根据获取到的uri 经过自己的dispatcher 访问自己的控制器 现在已经通过netty的http获取到  http请求了
		
		
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set("Content-Type", "text/html;charset=UTF-8");
		StringBuilder buf = new StringBuilder("hello:zsq");
		ByteBuf buffer = Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
		response.content().writeBytes(buffer);
		buffer.release();
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}


	
	
	
	
}
