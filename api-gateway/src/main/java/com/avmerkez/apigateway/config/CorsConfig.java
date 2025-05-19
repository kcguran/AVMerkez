package com.avmerkez.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Tüm kaynaklara izin ver (üretimde sınırlandırılabilir)
        config.addAllowedOrigin("*");
        // Alternatif olarak spesifik origins: 
        // config.setAllowedOrigins(Arrays.asList("https://avmerkez.com", "https://admin.avmerkez.com"));
        
        // Credentials için izin (varsayılan olarak kapalı)
        // config.setAllowCredentials(true);
        
        // İzin verilen HTTP metodları
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // İzin verilen HTTP başlıkları
        config.setAllowedHeaders(Arrays.asList(
                "Origin", 
                "Content-Type", 
                "Accept", 
                "Authorization", 
                "X-Requested-With", 
                "Access-Control-Request-Method", 
                "Access-Control-Request-Headers"));
                
        // İstemcinin önbellekte tutabileceği süre (saniye)
        config.setMaxAge(3600L);
        
        // İstemciye dönülen başlıklar
        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin", 
                "Access-Control-Allow-Methods", 
                "Access-Control-Allow-Headers", 
                "Access-Control-Max-Age", 
                "Access-Control-Allow-Credentials"));
                
        // Tüm URL'ler için uygula
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
} 