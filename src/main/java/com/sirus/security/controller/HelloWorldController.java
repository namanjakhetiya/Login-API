package com.sirus.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

	@RequestMapping("/hello")
	public String helloWorld() {
		return "Hello World!";
	}
}
