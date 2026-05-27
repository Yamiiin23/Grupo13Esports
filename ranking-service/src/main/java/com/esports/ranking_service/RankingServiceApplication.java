package com.esports.ranking_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- 1. Asegúrate de importar esto

@SpringBootApplication
@EnableFeignClients
public class RankingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RankingServiceApplication.class, args);
	}

}
