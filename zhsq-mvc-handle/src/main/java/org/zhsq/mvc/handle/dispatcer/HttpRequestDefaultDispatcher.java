package org.zhsq.mvc.handle.dispatcer;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.OrderComparator;
import org.zhsq.mvc.handle.argumentresolver.ArgResolver;
import org.zhsq.mvc.handle.argumentresolver.ArgResolverFactory;
import org.zhsq.mvc.handle.exception.ExceptionResolver;
import org.zhsq.mvc.handle.exception.HttpDefaultExceptionResolver;
import org.zhsq.mvc.handle.filter.HttpFilter;
import org.zhsq.mvc.handle.handle.DefaultAnnotationHandlermapping;
import org.zhsq.mvc.handle.intercepter.HttpIntercepter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

/**
 * HTTP协议请求默认调度控制器
 * @author zhaosq
 * @date 2018年5月11日
 * @since 1.0
 */
public class HttpRequestDefaultDispatcher implements HttpDispatcher, ApplicationContextAware, Serializable {

	private static final long serialVersionUID = -6650668113850987221L;
	private transient ApplicationContext webApplicationContext;
	private transient DefaultAnnotationHandlermapping handlermapping;
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestDefaultDispatcher.class);

	/**
	 * 当请求路径前缀匹配 prefix时调度器才会起作用
	 */
	private String prefix;
	/**
	 * 为当前调度器配置拦截器
	 */
	private transient List<HttpIntercepter> interceptors;
	/**
	 * 为当前调度器配置过滤器
	 */
	private transient List<HttpFilter> filters;

	private transient ExceptionResolver exceptionResolver;


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

		boolean checkResult = doIntercepterAndFiler(request, response);

		if (!checkResult) {
			return ;
		}

		doInvoker(request, response);

	}

	private void doInvoker(FullHttpRequest request, FullHttpResponse response) {
		if (handlermapping == null) {
			if (webApplicationContext != null) {
				handlermapping = 
						webApplicationContext.getBean(DefaultAnnotationHandlermapping.class);
			} else {
				LOGGER.error("获取hander处理器失败，因为没有找到对应的webApplicationContext:{}",webApplicationContext);
				response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
				return ;
			}
		}
		//根据uri获取处理类
		Object handlerType = handlermapping.getTypeHandler(request.uri());
		//根据uri获取处理方法
		Method handlerMethod = handlermapping.getMethodHandler(request.uri());

		if (handlerType == null || handlerMethod == null) {
			LOGGER.error("根据uri获取处理类和处理方法失败，因为此uri没有找到对应的@RequestMapping()标注类和方法,uri:{}",request.uri());
			response.setStatus(HttpResponseStatus.BAD_REQUEST);
			return ;
		}
		Method handlerMethodToInvoke = BridgeMethodResolver.findBridgedMethod(handlerMethod);
		//获取参数解析策略并解析参数
		ArgResolver resolver = ArgResolverFactory.createResolver(request.headers().get("Content-Type"));
		//解析出来的参数
		Object[] args = resolver.doResolve(request, handlerMethod);

		ByteBuf buffer = null;
		try {
			Object result = handlerMethodToInvoke.invoke(handlerType, args);
			//TODO 此处设计不合理，只有在返回数据为json字符串的时候才对，这里应该有不同的返回结果解析器，比如将返回结果解析为application/json，text/html等，
			//应该让框架使用者在 RequestMappring标注的方法上再添加注解(或者就在RequestMappring注解中)添加返回结果的类型。
			response.headers().set("Content-Type", "application/json;charset=UTF-8");
			StringBuilder buf = new StringBuilder(result.toString());
			buffer = Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
			response.content().writeBytes(buffer);
			buffer.release();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			//TODO 将异常抛给框架使用者
			dealException(request, response);
		} finally {
			if (buffer != null) {
				buffer.clear();
			}
		}
	}

	private void dealException(FullHttpRequest request, FullHttpResponse response) {
		if (exceptionResolver == null) {
			exceptionResolver = ExceptionResolverHolder.DEFAULT_EXCEPTION_RESOLVER;
		}
		exceptionResolver.dealException(request, response);
	}

	private boolean doIntercepterAndFiler(FullHttpRequest request, FullHttpResponse response) {
		//拦截链拦截结果默认为true
		boolean intercepterd = true;
		if (interceptors != null && interceptors.size() != 0) {
			//对interceptors进行排序
			Collections.sort(interceptors, OrderComparator.INSTANCE);
			for (HttpIntercepter intercepter : interceptors) {
				if (!intercepter.intercept(request, response)) {
					intercepterd = false;
					break;
				}
			}
		}

		if (!intercepterd) {
			//拦截链处理逻辑未通过 返回
			return false;
		}

		if (filters != null && filters.size() != 0) {
			//对filters进行排序
			Collections.sort(filters, OrderComparator.INSTANCE);
			//对请求进行过滤
			for (HttpFilter filter : filters) {
				filter.doFilter(request, response);
			}
		}
		return true;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		webApplicationContext = applicationContext; 		
	}

	private static class ExceptionResolverHolder{
		static ExceptionResolver DEFAULT_EXCEPTION_RESOLVER = new HttpDefaultExceptionResolver();
	}

}
