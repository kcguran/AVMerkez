package com.avmerkez.storeservice.config;

import com.avmerkez.storeservice.security.AuthEntryPointJwt;
import com.avmerkez.storeservice.security.AuthTokenFilter;
// UserDetailsServiceImpl UserDetailsService implementasyonu olduğu için Spring Security tarafından bulunacaktır.
// import com.avmerkez.storeservice.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor // AuthTokenFilter ve AuthEntryPointJwt enjekte etmek için
public class SecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final AuthEntryPointJwt unauthorizedHandler;

    private static final String[] PUBLIC_GET_ENDPOINTS = {
        "/api/v1/stores/**",
        "/api/v1/categories/**",
        "/api/v1/brands/**",
        "/actuator/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };

    private static final String[] ADMIN_MUTATE_ENDPOINTS = {
            "/api/v1/categories/**",
            "/api/v1/brands/**"
    };
     private static final String[] ADMIN_OR_MALL_MANAGER_MUTATE_ENDPOINTS = {
            "/api/v1/stores/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.POST, ADMIN_MUTATE_ENDPOINTS).hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, ADMIN_MUTATE_ENDPOINTS).hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, ADMIN_MUTATE_ENDPOINTS).hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, ADMIN_OR_MALL_MANAGER_MUTATE_ENDPOINTS).hasAnyAuthority("ROLE_ADMIN", "ROLE_MALL_MANAGER")
                .requestMatchers(HttpMethod.PUT, ADMIN_OR_MALL_MANAGER_MUTATE_ENDPOINTS).hasAnyAuthority("ROLE_ADMIN", "ROLE_MALL_MANAGER")
                .requestMatchers(HttpMethod.DELETE, ADMIN_OR_MALL_MANAGER_MUTATE_ENDPOINTS).hasAnyAuthority("ROLE_ADMIN", "ROLE_MALL_MANAGER")
                .anyRequest().authenticated()
            );

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
} 