package org.zhsq.mvc.handle.context;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.zhsq.mvc.handle.handle.DefaultAnnotationHandlermapping;

/**
 * @author zhaosq
 * @date 2018年5月13日
 * @since 1.0
 */
public class ZhsqWebApplicationContext {
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

		//加载webApplicationContext上下文
		webApplicationContext = new ClassPathXmlApplicationContext(configLocation.split("[,\\s]+"));
		webApplicationContext.setParent(rootApplicationContext);
		webApplicationContext.start();

		//获取beanfactory
		DefaultListableBeanFactory factory = (DefaultListableBeanFactory) webApplicationContext.getAutowireCapableBeanFactory();

		/**通过beanFactory获取并注册bean*/

		//将DefaultAnnotationHandlermapping注册为单实例bean,并调用其init方法对@RequestMapping()注解的bean进行捕获
		//以便以便后期根据URL获取handler
		DefaultAnnotationHandlermapping bean = factory.createBean(DefaultAnnotationHandlermapping.class);
		factory.registerSingleton(DefaultAnnotationHandlermapping.class.getName(), bean);

	}


	public void stop(){
		if (webApplicationContext != null) {
			webApplicationContext.stop();
			webApplicationContext.close();
			webApplicationContext = null;
		}
	}
}
