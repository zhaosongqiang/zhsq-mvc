package org.zhsq.mvc.handle.handle;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class AnnotationMethodHandler implements BeanFactoryPostProcessor {

	//缓存webApplicationContext中RequestMapping注解的类和方法解
	private static final Map<Class<?>, List<Map<String, Method>>> cachedClassMethod = new ConcurrentHashMap<>(100);

	public void invokeHandlerMethod (FullHttpRequest request, FullHttpResponse response, Object handler) {

		//		Class<?> handlerClass = ClassUtils.getUserClass(handler);
		//		//		String lookupPath = request.uri()
		//		String lookupPath = "/test/hello";
		//
		//		//		ServletHandlerMethodResolver methodResolver = getMethodResolver(handler);
		//
		//
		//		Method handlerMethod = methodResolver.resolveHandlerMethod(request);
		//		Method handlerMethodToInvoke = BridgeMethodResolver.findBridgedMethod(handlerMethod);
		//
		//		Object[] args = new Object[1];
		//
		//		return handlerMethodToInvoke.invoke(handler, args);


	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {



	}

}
