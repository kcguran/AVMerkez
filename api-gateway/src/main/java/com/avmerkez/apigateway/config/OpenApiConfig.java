package com.avmerkez.apigateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;

import java.util.ArrayList;
import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AVMerkez API Gateway",
                version = "1.0.0",
                description = "AVMerkez uygulaması için API Gateway",
                contact = @Contact(
                        name = "AVMerkez Team",
                        email = "info@avmerkez.com"
                ),
                license = @License(
                        name = "Proprietary",
                        url = "https://avmerkez.com"
                )
        ),
        servers = {
                @Server(url = "/", description = "Default Server")
        }
)
public class OpenApiConfig {

    @Bean
    public List<GroupedOpenApi> apis(
            SwaggerUiConfigParameters swaggerUiConfigParameters,
            RouteDefinitionLocator locator) {

        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        
        // Mikroservis grupları
        // Her servis için bir API grubu oluştur
        definitions.stream()
                .filter(routeDefinition -> routeDefinition.getId().matches("user-service|mall-service|store-service|review-service"))
                .forEach(routeDefinition -> {
                    String name = routeDefinition.getId();
                    
                    // SwaggerUI panelinde görünecek isim için parametre ekle
                    swaggerUiConfigParameters.addGroup(name);
                    
                    // Swagger UI grubu tanımı 
                    GroupedOpenApi.builder()
                            .pathsToMatch("/" + name + "/**")
                            .group(name)
                            .build();
                    
                    // API dokümantasyonu için mikroservis belgelendirmesinin yolunu ekle
                    // Bu, v3/api-docs/{serviceId} yolunu açar ve ilgili servise bağlanır
                    groups.add(GroupedOpenApi.builder()
                            .group(name)
                            .pathsToMatch("/api/v1/" + (name.replace("-service", "")) + "/**")
                            .build());
                });
        
        return groups;
    }
} 