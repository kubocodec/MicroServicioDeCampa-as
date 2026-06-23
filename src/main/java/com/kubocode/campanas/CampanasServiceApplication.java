package com.kubocode.campanas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CampanasServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampanasServiceApplication.class, args);
	}

}
