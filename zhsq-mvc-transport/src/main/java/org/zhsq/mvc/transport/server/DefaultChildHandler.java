package org.zhsq.mvc.transport.server;

import org.zhsq.mvc.handle.dispatcer.HttpRequestDefaultDispatcher;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author zhaosq
 * @date 2018年5月11日
 */
class DefaultChildHandler extends ChannelInitializer<SocketChannel>{
	private HttpRequestDefaultDispatcher dispatcherRef;

	public DefaultChildHandler(HttpRequestDefaultDispatcher dispatcherRef) {
		this.dispatcherRef = dispatcherRef;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
		ch.pipeline().addLast("http-agregator",new HttpObjectAggregator(65536));
		ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
		ch.pipeline().addLast("http-server",new NettyServerHandler(dispatcherRef));
	}



}
