package org.zhsq.mvc.web.suport;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhaosq
 * @date 2018年5月13日
 * @since 1.0
 */
class ZhsqWebApplicationContext {
	private ApplicationContext rootApplicationContext;
	private String configLocation;
	private ClassPathXmlApplicationContext webApplicationContext;

	public ZhsqWebApplicationContext (ApplicationContext parentContext, String configLocation) {
		this.rootApplicationContext = parentContext;
		this.configLocation = configLocation;
	}

	public void start() {
		if (configLocation == null || configLocation.length() == 0) {
			throw new RuntimeException("请为WebApplicationContextLoaderConfig指定configLocation值...");
		}

		webApplicationContext = new ClassPathXmlApplicationContext(configLocation.split("[,\\s]+"));
		webApplicationContext.setParent(rootApplicationContext);
		webApplicationContext.start();
	}


	public void stop(){
		if (webApplicationContext != null) {
			webApplicationContext.stop();
			webApplicationContext.close();
			webApplicationContext = null;
		}
	}
}
