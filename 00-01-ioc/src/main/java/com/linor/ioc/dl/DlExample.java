package com.linor.ioc.dl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DlExample {

	public static void main(String... args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("xmlContext.xml");
		Hello hello = (Hello)context.getBean("hello");
		System.out.println(hello.sayHello());
	}

}
