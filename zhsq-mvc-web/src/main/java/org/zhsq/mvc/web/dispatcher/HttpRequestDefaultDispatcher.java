package org.zhsq.mvc.web.dispatcher;

import java.util.ArrayList;
import java.util.List;

import org.zhsq.mvc.web.filter.Filter;
import org.zhsq.mvc.web.intercepter.HttpIntercepter;
import org.zhsq.mvc.web.intercepter.Intercepter;

/**
 * HTTP协议请求默认调度控制器
 * @author zhaosq
 * @date 2018年5月11日
 * @since 1.0
 */
public class HttpRequestDefaultDispatcher {

	/**
	 * 当请求路径前缀匹配 prefix时调度器才会起作用
	 */
	private String prefix;

	/**
	 * web上下文配置文件加载路径
	 */
	private String contextConfigLocation;
	/**
	 * 为当前调度器配置拦截器
	 */
	private final List<HttpIntercepter> interceptors = new ArrayList<HttpIntercepter>(10);
	/**
	 * 为当前调度器配置过滤器
	 */
	private final List<Filter> filters = new ArrayList<Filter>(10);


	//	我觉得是在 dispatcher 中维护自己的 拦截器链和过滤器链 
	//	包括生成拦截器链和过滤器链  以及 调用下一个 拦截器和过滤器











}
