package org.zhsq.mvc.web.suport;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.ApplicationContext;

/**
 * @author zhaosq
 * @date 2018年5月13日
 * @since 1.0
 */
class WebApplicationContextLoaderTask implements Runnable {

	private ApplicationContext rootApplicationContext;
	private String configLocation;

	public WebApplicationContextLoaderTask (ApplicationContext parentContext, String configLocation) {
		this.rootApplicationContext = parentContext;
		this.configLocation = configLocation;
	}

	@Override
	public void run() {
		// 初始化web ApplicationContext 不能影响root ApplicationContext的加载
		System.out.println("===========================================================================================");
		System.out.println("===============欢迎使用Zhsq-mvc框架 https://github.com/zhaosongqiang/zhsq-mvc.git===============");
		System.out.println("===========================================================================================");

		ZhsqWebApplicationContext context = new ZhsqWebApplicationContext(rootApplicationContext, configLocation);

		//给虚拟机添加一个钩子，在虚拟机准备停止的时候关闭 WebApplicationContext
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				context.stop();
			}
		});
		context.start();
		System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " Zhsq-mvc 启动成功!");

	}

}
