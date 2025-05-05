package com.example.FournisseurService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@SpringBootApplication
@EnableEurekaClient
public class FournisseurServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FournisseurServiceApplication.class, args);
	}

}
