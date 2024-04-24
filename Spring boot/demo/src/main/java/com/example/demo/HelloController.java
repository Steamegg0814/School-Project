package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@RequestMapping(value = "/")
	public String spring() {
		return "Spring Boot!!";
	}
	
	@RequestMapping(value = "/hello")
	public String hello() {
		return "Hello World!!";
	}

}
