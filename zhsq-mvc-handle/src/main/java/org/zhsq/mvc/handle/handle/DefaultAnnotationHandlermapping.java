package org.zhsq.mvc.handle.handle;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.zhsq.mvc.handle.annotation.RequestMapping;

/**
 * webApplicationContext加载后，对org.zhsq.mvc.handle.annotation.RequestMapping注解
 * 的bean 以及方法进行捕获并放入handlerMap，以便后期根据URL获取handler
 * @author zhaosq
 * @date 2018年5月13日
 * @since 1.0
 */
public class DefaultAnnotationHandlermapping implements ApplicationContextAware,InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAnnotationHandlermapping.class);

	private ApplicationContext webApplicationContext;

	private boolean useDefaultSuffixPattern = true;

	private boolean lazyInitHandlers = false;

	/**
	 * 存放所有webApplicationContext中被@RequestMapping 注解的uri以及对应的class
	 */
	private final Map<String, Object> typeMapping = new LinkedHashMap<String, Object>(100);
	/**
	 * 存放所有webApplicationContext中被@RequestMapping 注解的uri以及对应Method
	 */
	private Map<String, Method> methodMapping = new HashMap<String, Method>(100);

	@Override
	public void afterPropertiesSet() {
		//获取webApplicationContext中所有被@RequestMapping注解的bean
		Map<String, Object> map = webApplicationContext.getBeansWithAnnotation(RequestMapping.class);

		for (Object bean : map.values()) {
			Class<?> clazz = bean.getClass();

			RequestMapping mappingTypeInfo = clazz.getAnnotation(RequestMapping.class);
			//获取@RequestMapping注解的bean 的所有@RequestMapping注解的path
			String[] typeLevelPaths = mappingTypeInfo.value();
			for(int i = 0; i <typeLevelPaths.length; i++){
				if (!typeLevelPaths[i].startsWith("/")){
					typeLevelPaths[i] = "/"+typeLevelPaths[i];
				}
				if (typeLevelPaths[i].endsWith("/")){
					typeLevelPaths[i] = typeLevelPaths[i].substring(0, typeLevelPaths[i].length()-1);
				}
			}

			//获取@RequestMapping注解的bean所有的方法
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(RequestMapping.class)) {
					RequestMapping mappingMethodInfo = method.getAnnotation(RequestMapping.class);
					String[] methodLevelPath = mappingMethodInfo.value();
					//缓存urlPath对应的Method
					setUrlToMethodMap(typeLevelPaths,methodLevelPath,method,bean);
				}
			}
		}
	}


	private void setUrlToMethodMap(String[] typeLevelPaths, String[] methodLevelPaths, Method method, Object bean) {
		for (String typeLevelPath : typeLevelPaths) {
			for (String methodLevelPath : methodLevelPaths) {
				if (!methodLevelPath.startsWith("/")) {
					methodLevelPath = "/"+methodLevelPath;
				}
				methodMapping.put(typeLevelPath+methodLevelPath, method);
				typeMapping.put(typeLevelPath+methodLevelPath, bean);
			}
		}
	}


	protected String[] determineUrlsForHandler(String beanName) {

		ApplicationContext context = webApplicationContext;
		Class<?> handlerType = context.getType(beanName);
		RequestMapping mapping = context.findAnnotationOnBean(beanName, RequestMapping.class);
		if (mapping != null) {
			// @RequestMapping found at type level
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
			}, ReflectionUtils.USER_DECLARED_METHODS);
		}
		return StringUtils.toStringArray(urls);
	}

	protected String[] determineUrlsForHandlerMethods(Class<?> handlerType) {
		//获取@RequestMapping注解的类中的  @RequestMapping 注解的方法 的路径
		List<String> methodUrls = new ArrayList<>(10);
		Method[] mehods = handlerType.getDeclaredMethods();
		for (Method method : mehods) {
			if (method.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping mapping = method.getAnnotation(RequestMapping.class);
				methodUrls.addAll(Arrays.asList(mapping.value()));
			}
		}
		return methodUrls.toArray(new String[0]);
	}

	public PathMatcher getPathMatcher() {
		return new AntPathMatcher();
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

		Object mappedHandler = this.typeMapping.get(urlPath);
		if (mappedHandler != null) {
			if (mappedHandler != resolvedHandler) {
				throw new IllegalStateException(
						"urlPath:"+urlPath+"映射 handlerMapping失败,其可能已经被映射到其他Handler了。");
			}
		}
		else {
			if ("/".equals(urlPath)) {
				LOGGER.debug("Root mapping to {}",getHandlerDescription(handler));
			}
			else if ("/*".equals(urlPath)) {
				LOGGER.debug("Default mapping to {}",getHandlerDescription(handler));
			}
			else {
				this.typeMapping.put(urlPath, resolvedHandler);
				LOGGER.debug("Mapped URL path {} onto {}",urlPath,getHandlerDescription(handler));
			}
		}
	}

	private String getHandlerDescription(Object handler) {
		return "handler " + (handler instanceof String ? "'" + handler + "'" : "of type [" + handler.getClass() + "]");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		webApplicationContext = applicationContext;
	}


	public Method getMethodHandler(String requestUri) {
		String uri = "";
		if (requestUri.indexOf("?") > -1) {
			uri = requestUri.substring(0, requestUri.indexOf("?"));
		} else {
			uri = requestUri;
		}
		return methodMapping.get(uri);
	}

	public Object getTypeHandler(String requestUri) {
		String uri = "";
		if (requestUri.indexOf("?") > -1) {
			uri = requestUri.substring(0, requestUri.indexOf("?"));
		} else {
			uri = requestUri;
		}
		return typeMapping.get(uri);
	}
}
