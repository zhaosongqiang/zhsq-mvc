package org.zhsq.mvc.handle.dispatcer;

import java.util.List;

import org.zhsq.mvc.handle.filter.HttpFilter;
import org.zhsq.mvc.handle.intercepter.HttpIntercepter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * HTTP协议请求默认调度控制器
 * @author zhaosq
 * @date 2018年5月11日
 * @since 1.0
 */
public class HttpRequestDefaultDispatcher implements HttpDispatcher {

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
	}









	//	我觉得是在 dispatcher 中维护自己的 拦截器链和过滤器链 
	//	包括生成拦截器链和过滤器链  以及 调用下一个 拦截器和过滤器









}
