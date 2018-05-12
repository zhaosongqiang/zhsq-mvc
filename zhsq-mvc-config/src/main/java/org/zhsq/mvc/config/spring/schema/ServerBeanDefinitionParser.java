package org.zhsq.mvc.config.spring.schema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 解析zhsq:server 标签
 * @author zhaosq
 * @date 2018年5月12日
 * @since 1.0
 */
public class ServerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	private final Class<?> beanClass;


	public ServerBeanDefinitionParser (Class<?> beanClass) {
		this.beanClass = beanClass;
	}


	@Override
	protected Class<?> getBeanClass(Element element) {
		return beanClass;
	}
	
	@Override
	protected boolean shouldGenerateId() {
		return true;
	}


	@Override
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		String name = element.getAttribute("name");
		String ip = element.getAttribute("ip");
		String port = element.getAttribute("port");
		String bossThreads = element.getAttribute("bossThreads");
		String workerThreads = element.getAttribute("workerThreads");

		if (StringUtils.hasText(name)) {
			bean.addPropertyValue("name", name);
		}
		if (StringUtils.hasText(ip)) {
			bean.addPropertyValue("ip", ip);
		}
		if (StringUtils.hasText(port)) {
			bean.addPropertyValue("port", port);
		}
		if (StringUtils.hasText(bossThreads)) {
			bean.addPropertyValue("bossThreads", bossThreads);
		}
		if (StringUtils.hasText(workerThreads)) {
			bean.addPropertyValue("workerThreads", workerThreads);
		}
	}
}
