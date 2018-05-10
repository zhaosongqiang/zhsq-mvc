package org.zhsq.mvc.transport.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;

public class DefaultChildHandler extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
		ch.pipeline().addLast("http-agregator",new HttpObjectAggregator(65536));
		ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
		ch.pipeline().addLast("http-server",new NettyServerHandler());
	}



}
