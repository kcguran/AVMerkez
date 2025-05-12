package com.avmerkez.storeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${springdoc.version:}") String appVersion,
            @Value("${spring.application.name}") String appName) {
        return new OpenAPI()
                .info(new Info()
                        .title(appName + " API")
                        .version(appVersion)
                        .description("API documentation for " + appName)
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
} 