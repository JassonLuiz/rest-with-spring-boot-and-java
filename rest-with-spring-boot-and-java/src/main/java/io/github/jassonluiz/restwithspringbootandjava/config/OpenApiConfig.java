package io.github.jassonluiz.restwithspringbootandjava.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
	
	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("RESTful API with Java 17 and Spring Boot 3")
						.version("v1")
						.description("API of study for Java 17 and Spring Boot 3"));
	}

}
