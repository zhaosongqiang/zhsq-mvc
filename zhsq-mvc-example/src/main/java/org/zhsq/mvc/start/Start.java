/**
 * 
 */
package org.zhsq.mvc.start;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhsq 
 * @date 2018年5月17日
 * @since 1.0
 */
public class Start {

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		System.setProperty("java.net.preferIPv4Stack", "true");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"META-INF/spring/*.xml"});
		context.start();

	}

}
