package com.gpp.servisoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServisoftApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServisoftApplication.class, args);
	}

}
