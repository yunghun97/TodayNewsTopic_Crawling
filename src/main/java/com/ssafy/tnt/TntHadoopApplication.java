package com.ssafy.tnt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TntHadoopApplication {

	public static void main(String[] args) {
		SpringApplication.run(TntHadoopApplication.class, args);
	}

}
