package com.avmerkez.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity; // Gateway WebFlux kullandığı için
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity // WebFlux tabanlı güvenlik konfigürasyonu
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for stateless API gateway
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Disable Basic Auth
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Disable Form Login
            .logout(ServerHttpSecurity.LogoutSpec::disable) // Disable Logout
            // Security context repository needs to be NoOp for stateless behavior with custom filter
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchanges -> exchanges
                // Actuator, swagger ve Eureka için açık erişim
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/actuator/health/**").permitAll() 
                .pathMatchers("/eureka/**").permitAll()
                .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/webjars/**").permitAll()
                // Add other paths that should bypass Spring Security entirely (if any)
                // Our AuthenticationFilter handles fine-grained path control
                .anyExchange().authenticated() // Require authentication handled by our filter
            );

        // The AuthenticationFilter (GlobalFilter) will handle the actual JWT validation.
        // We configure Spring Security here mainly to disable default protections and
        // potentially set up authentication entry points if needed, but AuthenticationFilter
        // intercepts before this chain for protected routes.

        return http.build();
    }
} 