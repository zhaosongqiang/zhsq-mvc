package org.zhsq.mvc.handle.dispatcer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zhsq.mvc.handle.filter.HttpFilter;
import org.zhsq.mvc.handle.handle.DefaultAnnotationHandlermapping;
import org.zhsq.mvc.handle.intercepter.HttpIntercepter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * HTTP协议请求默认调度控制器
 * @author zhaosq
 * @date 2018年5月11日
 * @since 1.0
 */
public class HttpRequestDefaultDispatcher implements HttpDispatcher, ApplicationContextAware {

	private ApplicationContext webApplicationContext;
	private DefaultAnnotationHandlermapping handlermapping;
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestDefaultDispatcher.class);

	/**
	 * 当请求路径前缀匹配 prefix时调度器才会起作用
	 */
	private String prefix;
	/**
	 * 为当前调度器配置拦截器
	 */
	private List<HttpIntercepter> interceptors;
	/**
	 * 为当前调度器配置过滤器
	 */
	private List<HttpFilter> filters;


	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<HttpIntercepter> getInterceptors() {
		return interceptors;
	}

	public List<HttpFilter> getFilters() {
		return filters;
	}

	public void setInterceptors(List<HttpIntercepter> interceptors) {
		this.interceptors = interceptors;
	}

	public void setFilters(List<HttpFilter> filters) {
		this.filters = filters;
	}

	@Override
	public void doDispatcher(FullHttpRequest request, FullHttpResponse response) {
		//拦截链拦截结果默认为true
		boolean intercepterd = true;
		if (interceptors != null && interceptors.size() != 0) {
			for (HttpIntercepter intercepter : interceptors) {
				if (!intercepter.intercept(request, response)) {
					intercepterd = false;
					break;
				}
			}
		}

		if (!intercepterd) {
			//拦截链处理逻辑未通过 返回
			return ;
		}

		if (filters != null && filters.size() != 0) {
			//对请求进行过滤
			for (HttpFilter filter : filters) {
				filter.doFilter(request, response);
			}
		}
		//TODO 获取handler

		if (handlermapping == null) {
			if (webApplicationContext != null) {
				handlermapping = 
						webApplicationContext.getBean(DefaultAnnotationHandlermapping.class);
			} else {
				LOGGER.error("获取hander处理器失败，因为没有对应的webApplicationContext");
				response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
				return ;
			}
		}

		//TODO 获取handler后 如何处理请参考springMvc  注意处理对request.uri()进行 prefix处理 以及参数处理
		Object handler = handlermapping.getHandler(request.uri());


	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		webApplicationContext = applicationContext; 		
	}
}
