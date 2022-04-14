package com.example.springboot.mycoolapp.rest;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@Value("${project.name}")
	private String projectName;

	@Value("${project.tools}")
	private String projectTools;

	@GetMapping("/")
	public String sayHello() {
		return "Hello ! Current time is " + LocalDateTime.now() +
				"\nThis app is a demo of : " + projectName +
				"\nTools being used : " + projectTools;
	}

	@GetMapping("/app1")
	public String app1() {
		return "Hello from app1";
	}

	@GetMapping("/app2")
	public String app2() {
		return "Hello from app2";
	}
}
