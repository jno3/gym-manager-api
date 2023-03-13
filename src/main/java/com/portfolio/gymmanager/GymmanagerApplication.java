package com.portfolio.gymmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
public class GymmanagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(GymmanagerApplication.class, args);
	}

}
