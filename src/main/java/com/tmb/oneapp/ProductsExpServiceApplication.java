package com.tmb.oneapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@ComponentScan(basePackages = { "com.tmb.oneapp", "com.tmb.common" })
public class ProductsExpServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductsExpServiceApplication.class);
	}
}
