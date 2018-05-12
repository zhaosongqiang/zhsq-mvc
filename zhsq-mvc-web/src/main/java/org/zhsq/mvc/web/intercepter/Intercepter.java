/**
 * 
 */
package org.zhsq.mvc.web.intercepter;

import java.util.List;

/**
 * 处理请求的拦截器
 * @author zhaosq
 * @date 2018年5月11日
 * @since 1.0
 */
public interface Intercepter {

	/**
	 * 获取拦截器链中的下一个拦截器
	 * @return 拦截器链中的下一个拦截器
	 */
	Intercepter next();

	/**
	 * 获取拦截器链
	 * @return 拦截器链
	 */
	List<Intercepter> intercepterChain();

}
