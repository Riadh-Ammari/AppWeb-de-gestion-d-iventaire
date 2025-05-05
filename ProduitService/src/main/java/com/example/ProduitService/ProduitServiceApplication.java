package com.example.ProduitService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableEurekaClient
@SpringBootApplication
public class ProduitServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProduitServiceApplication.class, args);
	}

}