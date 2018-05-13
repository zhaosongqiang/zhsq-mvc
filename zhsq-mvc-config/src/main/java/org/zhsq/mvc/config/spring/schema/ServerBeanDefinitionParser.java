package org.zhsq.mvc.config.spring.schema;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
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
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder bean) {
		String name = element.getAttribute("name");
		String ip = element.getAttribute("ip");
		String port = element.getAttribute("port");
		String bossThreads = element.getAttribute("bossThreads");
		String workerThreads = element.getAttribute("workerThreads");
		String dispatcherRef = element.getAttribute("dispatcherRef");

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
		if (StringUtils.hasText(dispatcherRef)) {

			if (parserContext.getRegistry().containsBeanDefinition(dispatcherRef)) {
				//如果dispatcherRef引用的bean已经解析，则验证这个bean是不是单实例的，不是的话就抛异常。
				BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(dispatcherRef);
				if (!refBean.isSingleton()) {
					throw new IllegalStateException("为zhsq:mvc指定的 dispatcherRef的scope必须为singleton，请设置dispatcherRef指定的bean的scope为singleton 例如：   <bean id=\"" + dispatcherRef + "\" scope=\"singleton\" ...>");
				}
			}

			Object reference = new RuntimeBeanReference(dispatcherRef);
			bean.addPropertyValue("dispatcherRef", reference);
		} else {
			throw new IllegalArgumentException("请为zhsq:server 指定一个scope为singletonBean的dispatcherRef");
		}
	}
}
