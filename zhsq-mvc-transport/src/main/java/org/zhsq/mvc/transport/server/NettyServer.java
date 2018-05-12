package org.zhsq.mvc.transport.server;

import org.zhsq.mvc.transport.server.exception.ServerBindException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.StringUtil;

/**
 * 请求监听服务
 * @author zhaosq
 * @date 2018年5月11日
 */
public class NettyServer {

	public void bind (String ip, int port, int boss, int worker) throws ServerBindException {

		EventLoopGroup bossGroup = new NioEventLoopGroup(boss == 0 ? 1 : boss);
		EventLoopGroup workerGroup = new NioEventLoopGroup(worker);

		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new DefaultChildHandler());

			ChannelFuture future = sb.bind(ip, port).sync();
			future.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			throw new ServerBindException("请求监听服务 绑定失败："+e.getMessage(),e);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main (String[] args) {
//		new NettyServer().bind("127.0.0.1",80,1,10);
	}


}
