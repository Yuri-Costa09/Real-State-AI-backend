package com.yuricosta.real_state_ai_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RealStateAiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealStateAiBackendApplication.class, args);
	}

}
