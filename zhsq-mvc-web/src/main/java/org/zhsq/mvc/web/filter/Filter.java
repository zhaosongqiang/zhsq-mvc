/**
 * 
 */
package org.zhsq.mvc.web.filter;

import java.util.List;

/**
 * @author zhaosq
 * @date 2018年5月11日
 * @since 1.0
 */
public interface Filter {

	/**
	 * 获取过滤器链中下一个过滤器
	 * @return 
	 */
	Filter next();
	
	/**获取过滤器链
	 * @return 过滤器链
	 */
	List<Filter> filterChain();
	
}
