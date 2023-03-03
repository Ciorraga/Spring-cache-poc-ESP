package com.pocsma.springcache.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class PocSpringDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocSpringDataApplication.class, args);
	}

}
