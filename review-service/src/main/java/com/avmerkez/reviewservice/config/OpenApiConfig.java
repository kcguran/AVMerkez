package com.avmerkez.reviewservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AVMerkez - Review Service API",
                version = "1.0",
                description = "AVM ve mağaza yorumlarını yönetme API'si",
                contact = @Contact(name = "AVMerkez Team", email = "info@avmerkez.com")
        ),
        servers = {
                @Server(url = "/", description = "Current Server"),
                @Server(url = "http://localhost:8085", description = "Local Server"),
                @Server(url = "https://api.avmerkez.com", description = "Production Server")
        },
        security = {
                @SecurityRequirement(name = "Cookie Auth")
        }
)
@SecurityScheme(
        name = "Cookie Auth",
        type = SecuritySchemeType.APIKEY,
        in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.COOKIE,
        paramName = "avm_jwt"
)
public class OpenApiConfig {
} 