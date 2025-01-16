package com.cromulent.cartio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.cromulent.cartio")
public class CartioApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartioApplication.class, args);
	}

}
