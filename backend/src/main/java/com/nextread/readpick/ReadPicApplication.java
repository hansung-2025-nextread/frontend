package com.nextread.readpick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ReadPicApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadPicApplication.class, args);
	}

}
