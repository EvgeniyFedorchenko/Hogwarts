package com.evgeniyfedorchenko.hogwarts;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition
@SpringBootApplication
public class HogwartsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HogwartsApplication.class, args);
	}
}
