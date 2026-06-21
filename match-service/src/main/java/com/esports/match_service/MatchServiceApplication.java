package com.esports.match_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // <-- ¡ESTA ANOTACIÓN ES OBLIGATORIA AQUÍ!
public class MatchServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MatchServiceApplication.class, args);
	}
}