package org.zhsq.mvc.config;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基础配置,主要是对一些基础配置进行定义和校验
 * @author zhaosq
 * @date 2018年5月12日
 * @since 1.0
 */
public abstract class AbstractConfig implements Serializable {

	private static final long serialVersionUID = -5013591462121099116L;

	private static final Pattern PATTERN_CONFIG_VALUE = Pattern.compile("[\\-._0-9a-zA-Z]+");

	private static final Pattern PATTERN_NUMBER = Pattern.compile("^\\d+$");

	private static final int MAX_LENGTH = 200;



	protected void checkProperty(String key, String value) {
		checkProperty(key,value,MAX_LENGTH,PATTERN_CONFIG_VALUE);
	}

	protected void checkProperty(String key, String value, Pattern pattern) {
		checkProperty(key,value,MAX_LENGTH,pattern);
	}

	protected void checkNumber(String key, String value) {
		checkProperty(key,value,MAX_LENGTH,PATTERN_NUMBER);
	}

	private static void checkProperty(String property, String value, int maxlength, Pattern pattern) {
		if (value == null || value.length() == 0) {
			return;
		}
		if (value.length() > maxlength) {
			throw new IllegalStateException("属性" + property + "=\"" + value + "\" 超过最大长度 " + maxlength);
		}
		if (pattern != null) {
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new IllegalStateException("属性 " + property + "=\"" + value + "\" 不能地匹配正则表达式:"+pattern.pattern());
			}
		}
	}





}
