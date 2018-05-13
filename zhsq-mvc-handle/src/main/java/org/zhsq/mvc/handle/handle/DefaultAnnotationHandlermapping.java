package org.zhsq.mvc.handle.handle;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.zhsq.mvc.handle.annotation.RequestMapping;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * webApplicationContext加载后，对org.zhsq.mvc.handle.annotation.RequestMapping注解
 * 的bean 以及方法进行捕获并放入handlerMap，以便后期根据URL获取handler
 * @author zhaosq
 * @date 2018年5月13日
 * @since 1.0
 */
public class DefaultAnnotationHandlermapping implements ApplicationContextAware,InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAnnotationHandlermapping.class);

	private final Map<Class<?>, RequestMapping> cachedMappings = new HashMap<Class<?>, RequestMapping>();

	private Object defaultHandler;

	private Object rootHandler;

	private ApplicationContext webApplicationContext;

	private PathMatcher pathMatcher = new AntPathMatcher();

	private boolean useDefaultSuffixPattern = true;
	private boolean lazyInitHandlers = false;

	private final Map<String, Object> handlerMap = new LinkedHashMap<String, Object>(100);

	@Override
	public void afterPropertiesSet() {
		String[] beanNames = webApplicationContext.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			String[] urls = determineUrlsForHandler(beanName);
			if (!ObjectUtils.isEmpty(urls)) {
				// URL paths found: Let's consider it a handler.
				registerHandler(urls, beanName);
			}
			else {
				LOGGER.debug("Rejected bean name {}: no URL paths identified",beanName);
			}
		}
	}


	protected String[] determineUrlsForHandler(String beanName) {

		ApplicationContext context = webApplicationContext;
		Class<?> handlerType = context.getType(beanName);
		RequestMapping mapping = context.findAnnotationOnBean(beanName, RequestMapping.class);
		if (mapping != null) {
			// @RequestMapping found at type level
			this.cachedMappings.put(handlerType, mapping);
			Set<String> urls = new LinkedHashSet<String>();
			String[] typeLevelPatterns = mapping.value();
			if (typeLevelPatterns.length > 0) {
				// @RequestMapping specifies paths at type level
				String[] methodLevelPatterns = determineUrlsForHandlerMethods(handlerType, true);
				for (String typeLevelPattern : typeLevelPatterns) {
					if (!typeLevelPattern.startsWith("/")) {
						typeLevelPattern = "/" + typeLevelPattern;
					}
					boolean hasEmptyMethodLevelMappings = false;
					for (String methodLevelPattern : methodLevelPatterns) {
						if (methodLevelPattern == null) {
							hasEmptyMethodLevelMappings = true;
						}
						else {
							String combinedPattern = getPathMatcher().combine(typeLevelPattern, methodLevelPattern);
							addUrlsForPath(urls, combinedPattern);
						}
					}
					if (hasEmptyMethodLevelMappings) {
						addUrlsForPath(urls, typeLevelPattern);
					}
				}
				return StringUtils.toStringArray(urls);
			}
			else {
				// actual paths specified by @RequestMapping at method level
				return determineUrlsForHandlerMethods(handlerType, false);
			}
		}
		else if (AnnotationUtils.findAnnotation(handlerType, Controller.class) != null) {
			// @RequestMapping to be introspected at method level
			return determineUrlsForHandlerMethods(handlerType, false);
		}
		else {
			return new String[0];
		}

	}

	protected String[] determineUrlsForHandlerMethods(Class<?> handlerType, final boolean hasTypeLevelMapping) {
		String[] subclassResult = determineUrlsForHandlerMethods(handlerType);
		if (subclassResult != null) {
			return subclassResult;
		}

		final Set<String> urls = new LinkedHashSet<String>();
		Set<Class<?>> handlerTypes = new LinkedHashSet<Class<?>>();
		handlerTypes.add(handlerType);
		handlerTypes.addAll(Arrays.asList(handlerType.getInterfaces()));
		for (Class<?> currentHandlerType : handlerTypes) {
			ReflectionUtils.doWithMethods(currentHandlerType, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) {
					RequestMapping mapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
					if (mapping != null) {
						String[] mappedPatterns = mapping.value();
						if (mappedPatterns.length > 0) {
							for (String mappedPattern : mappedPatterns) {
								if (!hasTypeLevelMapping && !mappedPattern.startsWith("/")) {
									mappedPattern = "/" + mappedPattern;
								}
								addUrlsForPath(urls, mappedPattern);
							}
						}
						else if (hasTypeLevelMapping) {
							// empty method-level RequestMapping
							urls.add(null);
						}
					}
				}
			}, ReflectionUtils.USER_DECLARED_METHODS);
		}
		return StringUtils.toStringArray(urls);
	}

	protected String[] determineUrlsForHandlerMethods(Class<?> handlerType) {
		return new String[0];
	}

	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	protected void addUrlsForPath(Set<String> urls, String path) {
		urls.add(path);
		if (this.useDefaultSuffixPattern && path.indexOf('.') == -1 && !path.endsWith("/")) {
			urls.add(path + ".*");
			urls.add(path + "/");
		}
	}

	protected void registerHandler(String[] urlPaths, String beanName) throws BeansException, IllegalStateException {
		Assert.notNull(urlPaths, "URL path array must not be null");
		for (String urlPath : urlPaths) {
			registerHandler(urlPath, beanName);
		}
	}

	protected void registerHandler(String urlPath, Object handler) throws BeansException, IllegalStateException {
		Assert.notNull(urlPath, "URL path must not be null");
		Assert.notNull(handler, "Handler object must not be null");
		Object resolvedHandler = handler;

		// Eagerly resolve handler if referencing singleton via name.
		if (!this.lazyInitHandlers && handler instanceof String) {
			String handlerName = (String) handler;
			if (webApplicationContext.isSingleton(handlerName)) {
				resolvedHandler = webApplicationContext.getBean(handlerName);
			}
		}

		Object mappedHandler = this.handlerMap.get(urlPath);
		if (mappedHandler != null) {
			if (mappedHandler != resolvedHandler) {
				throw new IllegalStateException(
						"urlPath:"+urlPath+"映射 handlerMapping失败,其可能已经被映射到其他Handler了。");
			}
		}
		else {
			if ("/".equals(urlPath)) {
				LOGGER.debug("Root mapping to {}",getHandlerDescription(handler));
				setRootHandler(resolvedHandler);
			}
			else if ("/*".equals(urlPath)) {
				LOGGER.debug("Default mapping to {}",getHandlerDescription(handler));
				setDefaultHandler(resolvedHandler);
			}
			else {
				this.handlerMap.put(urlPath, resolvedHandler);
				LOGGER.debug("Mapped URL path {} onto {}",urlPath,getHandlerDescription(handler));
			}
		}
	}

	private String getHandlerDescription(Object handler) {
		return "handler " + (handler instanceof String ? "'" + handler + "'" : "of type [" + handler.getClass() + "]");
	}

	public void setDefaultHandler(Object defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	public void setRootHandler(Object rootHandler) {
		this.rootHandler = rootHandler;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		webApplicationContext = applicationContext;
	}


	public Object getHandler(String requestUri) {
		return handlerMap.get(requestUri);
	}

}
