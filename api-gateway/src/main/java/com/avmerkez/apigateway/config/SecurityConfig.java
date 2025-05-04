package com.avmerkez.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity; // Gateway WebFlux kullandığı için
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // WebFlux tabanlı güvenlik konfigürasyonu
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // CSRF korumasını devre dışı bırak
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            // HTTP Basic'i devre dışı bırak
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            // Form Login'i devre dışı bırak
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            // Yetkilendirme kuralları
            .authorizeExchange(exchanges -> exchanges
                // Şimdilik tüm isteklere izin ver
                .anyExchange().permitAll()
                // TODO: Gerçek yetkilendirme kuralları eklenecek
                // Örn: .pathMatchers("/api/v1/auth/**").permitAll()
                // Örn: .pathMatchers("/mall-service/api-docs/**", "/mall-service/swagger-ui.html").permitAll() // Swagger UI erişimi
                // Örn: .anyExchange().authenticated()
            );

        // TODO: JWT doğrulama filtresi gibi Global Filter'lar eklenecek

        return http.build();
    }
} 