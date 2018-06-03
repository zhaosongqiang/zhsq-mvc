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
 * 的bean 以及方法进行捕获并放入handlerMap，以便后期根据URL获取handler,此bean在ZhsqWebApplicationContext中加载
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
