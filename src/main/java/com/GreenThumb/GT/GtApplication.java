package com.GreenThumb.GT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class GtApplication {

	public static void main(String[] args) {
		SpringApplication.run(GtApplication.class, args);
	}

}
