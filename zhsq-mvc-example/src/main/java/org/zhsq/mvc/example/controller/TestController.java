package org.zhsq.mvc.example.controller;

import org.springframework.stereotype.Controller;
import org.zhsq.mvc.handle.annotation.RequestMapping;

/**
 * 测试   DefaultAnnotationHandlermapping 捕获所有的 @RequestMapping 注解的bean
 * @author zhsq 
 * @date 2018年5月13日
 * @since 1.0
 */
@Controller
@RequestMapping("/test")
public class TestController {

	@RequestMapping("hello")
	public String teString () {
		System.out.println("hello");
		return "zsq";
	}

}
