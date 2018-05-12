package org.zhsq.mvc.config.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.zhsq.mvc.config.ServerConfig;


/**
 * xml文件中"zhsq:xxx" 标签解析器
 * @author zhaosq
 * @date 2018年5月12日
 * @since 1.0
 */
public class ZhsqNameSpaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		//请求监听服务启动配置解析器
		registerBeanDefinitionParser("server", new ServerBeanDefinitionParser(ServerConfig.class));
	}
}
