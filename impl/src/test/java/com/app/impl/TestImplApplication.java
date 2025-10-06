package com.app.impl;

import org.springframework.boot.SpringApplication;

public class TestImplApplication {

	public static void main(String[] args) {
		SpringApplication.from(ImplApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
