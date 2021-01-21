package io.github.u2ware.data.test.example03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.querydsl.EntityPathResolver;

import io.github.u2ware.data.jpa.repository.support.QuerydslSimpleEntityPathResolver;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	
	//////////////////////////////////////////////
	//  When you don't have "Querydsl Q-File". 
	//////////////////////////////////////////////
	@Bean 
	public EntityPathResolver extendedSimpleEntityPathResolver() {
		return new QuerydslSimpleEntityPathResolver();
	}
}

