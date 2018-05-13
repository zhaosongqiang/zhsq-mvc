/**
 * 
 */
package org.zhsq.mvc.config;

import java.util.regex.Pattern;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.zhsq.mvc.handle.dispatcer.HttpRequestDefaultDispatcher;
import org.zhsq.mvc.transport.server.NettyServer;

import io.netty.util.internal.StringUtil;

/**
 * netty服务监听获取请求信息
 * @author zhaosq
 * @date 2018年5月12日
 * @since 1.0
 */
public class ServerConfig extends AbstractConfig implements ApplicationListener<ContextRefreshedEvent>,DisposableBean {


	private static final long serialVersionUID = -3326397664502103849L;

	private static final Pattern PATTERN_IPV4 = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

	private static final int MAX_PORT = 65535;

	private static final String DEFAULT_IP = "127.0.0.1";

	private transient NettyServer nettyServer;

	private String name;

	private String ip;

	private int port;

	private int bossThreads;

	private int workerThreads;
	//服务接收请求，后的分派处理器
	private HttpRequestDefaultDispatcher dispatcherRef;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBossThreads() {
		return bossThreads;
	}

	public void setBossThreads(int bossThreads) {
		this.bossThreads = bossThreads;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public HttpRequestDefaultDispatcher getDispatcherRef() {
		return dispatcherRef;
	}

	public void setDispatcherRef(HttpRequestDefaultDispatcher dispatcherRef) {
		this.dispatcherRef = dispatcherRef;
	}

	@Override
	public String toString() {
		return "ServerConfig [name=" + name + ", ip=" + ip + ", port=" + port
				+ ", bossThreads=" + bossThreads + ", workerThreads=" + workerThreads + "]";
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//校验ip port 以及超时参数
		if (StringUtil.isNullOrEmpty(ip)) {
			ip = DEFAULT_IP;
		}

		checkProperty("ip", ip,PATTERN_IPV4);
		checkPort();
		checkDispatcherRef();

		//开启请求监听服务
		startServer();
	}

	/**
	 * 
	 */
	private void checkDispatcherRef() {
		if (dispatcherRef == null) {
			throw new IllegalArgumentException("必须为zhsq:server 指定dispatcherRef");
		}
	}

	private void startServer() {
		nettyServer = new NettyServer(bossThreads, workerThreads, dispatcherRef);
		nettyServer.bind(ip, port);
		System.out.println("============================================================");
		System.out.println("==========请求监听服务绑定成功,监听地址："+ip+":"+port+"==============");
		System.out.println("============================================================");
	}

	private void checkPort() {
		if (port == 0) {
			//服务监听端口默认为 80
			port = 80;
			return ;
		}
		if (port < 0 || port > MAX_PORT) {
			throw new IllegalArgumentException("端口号必须介于 0 - 66535 之间!");
		}
	}

	@Override
	public void destroy() throws Exception {
		if (nettyServer != null) {
			//关闭请求监听服务
			nettyServer.shutdown();
			nettyServer = null;
			System.out.println("============================================================");
			System.out.println("=========解除请求监听服务绑定成功,监听地址："+ip+":"+port+"============");
			System.out.println("============================================================");
		}
	}
}
