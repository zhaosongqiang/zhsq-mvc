package org.zhsq.mvc.example.controller;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.zhsq.mvc.example.model.Student;
import org.zhsq.mvc.handle.annotation.RequestMapping;

/**
 * 测试   DefaultAnnotationHandlermapping 捕获所有的 @RequestMapping 注解的bean
 * @author zhsq 
 * @date 2018年5月13日
 * @since 1.0
 */
@Controller
@RequestMapping("/zsq")
public class TestController {



	@RequestMapping("hello")
	public String hello (String name,int age, Integer no, boolean bb, Student student,Date date) {
		System.out.println(student.toString());
		return "hello:"+name+","+age+","+no;
	}

	@RequestMapping("welcome")
	public String welcome () {
		return "welcome to use the Zhsq-mvc fremawork";
	}

}
