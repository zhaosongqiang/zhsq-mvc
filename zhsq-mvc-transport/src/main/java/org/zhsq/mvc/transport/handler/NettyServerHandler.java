package org.zhsq.mvc.transport.handler;

import org.zhsq.mvc.handle.dispatcer.HttpRequestDefaultDispatcher;

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

	private HttpRequestDefaultDispatcher dispatcherRef;

	public NettyServerHandler(HttpRequestDefaultDispatcher dispatcherRef) {
		this.dispatcherRef = dispatcherRef;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

		if (dispatcherRef == null) {
			response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
		} else {
			dispatcherRef.doDispatcher(request, response);
		}
		
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}






}
