package br.com.curso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
					.title("RestFul API with Java 17 and Spring boot 3")
					.version("v1")
					.description("Some description about yout API")
					.termsOfService("https://github.com/RuanVSouza")
					.license(
						new License()
							.name("Apache 2.0")
							.url("https://github.com/RuanVSouza")
							)
					);
	}
}
