package org.zhsq.mvc.web.suport;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * 获取root ApplicationContext,将其传递给 web ApplicationContext 作为其父容器
 * 并配置web ApplicationContext的加载的xml文件的路径
 * @author zhaosq
 * @date 2018年5月13日
 * @since 1.0
 */
public class WebApplicationContextLoaderConfig implements DisposableBean, ApplicationListener<ContextRefreshedEvent> {

	@NonNull
	private String configLocation;

	public String getConfigLocation() {
		return configLocation;
	}

	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	@Override
	public void destroy() throws Exception {
		//TODO 在root ApplicationContext中MvcLoaderConfig被销毁时销毁this.applicationContext
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//获取root ApplicationContext
		ApplicationContext parentContext = event.getApplicationContext();
		//异步初始化web ApplicationContext 不能影响root ApplicationContext的加载

		if (StringUtils.isEmpty(configLocation)) {
			throw new RuntimeException("请为WebApplicationContextLoaderConfig指定configLocation值...");
		}

		Thread thread = new Thread(new WebApplicationContextLoaderTask(parentContext, configLocation), 
				"WebApplicationContextLoaderStartThread");
		thread.start();
	}

}
