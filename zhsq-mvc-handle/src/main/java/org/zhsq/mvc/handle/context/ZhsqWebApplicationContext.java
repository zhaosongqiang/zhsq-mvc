package org.zhsq.mvc.handle.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhaosq
 * @date 2018年5月13日
 * @since 1.0
 */
public class ZhsqWebApplicationContext {
	private ApplicationContext rootApplicationContext;
	private String configLocation;
	private ClassPathXmlApplicationContext webApplicationContext;

	private static final Logger LOGGER = LoggerFactory.getLogger(ZhsqWebApplicationContext.class);



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
