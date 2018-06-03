package org.zhsq.mvc.transport.server;

import org.zhsq.mvc.handle.dispatcer.HttpRequestDefaultDispatcher;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.zhsq.mvc.transport.handler.NettyServerHandler;
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

		//TODO 引入粘包拆包
		ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
		//HttpObjectAggregator将多个消息转化成单一的FullHttpRequest或者FullHttpResonse。因为HttpRequestDecoder在每个Http消息中会生成多个消息对象
		//HttpContent,HttpRequest,HttpResonse,LastHttpContent等
		ch.pipeline().addLast("http-agregator",new HttpObjectAggregator(65536));
		ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
		ch.pipeline().addLast("http-server",new NettyServerHandler(dispatcherRef));
	}



}
