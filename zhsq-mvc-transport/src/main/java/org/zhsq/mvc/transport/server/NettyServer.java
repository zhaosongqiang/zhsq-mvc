package org.zhsq.mvc.transport.server;

import org.zhsq.mvc.handle.dispatcer.HttpRequestDefaultDispatcher;
import org.zhsq.mvc.transport.server.exception.ServerBindException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 请求监听服务
 * @author zhaosq
 * @date 2018年5月11日
 */
public class NettyServer {

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private HttpRequestDefaultDispatcher dispatcherRef;

	public NettyServer (int boss, int worker, HttpRequestDefaultDispatcher dispatcherRef) {
		this.bossGroup = new NioEventLoopGroup(boss == 0 ? 1 : boss);
		this.workerGroup = new NioEventLoopGroup(worker);
		this.dispatcherRef = dispatcherRef;
	}

	public void bind (String ip, int port) throws ServerBindException {

		if (bossGroup == null || workerGroup == null) {
			throw new RuntimeException("请求监听服务绑定失败，未获取到请求处理线程组或者IO读写处理线程组...");
		}

		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new DefaultChildHandler(dispatcherRef));

			ChannelFuture future = sb.bind(ip, port).sync();
			future.awaitUninterruptibly();

			if (!future.isSuccess()) {
				future.channel().close().awaitUninterruptibly();
				throw new RuntimeException("请求监听服务绑定失败: " + ip+":"+port, future.cause());
			}
		} catch (InterruptedException e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			throw new ServerBindException("请求监听服务 绑定失败："+e.getMessage(),e);
		}
	}


	/**
	 * 关闭请求监听服务
	 */
	public void shutdown() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

}
