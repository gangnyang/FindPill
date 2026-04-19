package com.example.med_classification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MedClassificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedClassificationApplication.class, args);
	}

}
