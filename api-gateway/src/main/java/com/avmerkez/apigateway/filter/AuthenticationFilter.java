package com.avmerkez.apigateway.filter;

import com.avmerkez.apigateway.security.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // List of paths that do not require authentication
    private final List<String> publicPaths = List.of(
            "/api/v1/auth/**", // Example: Auth endpoints (if any were public)
            "/eureka/**",      // Allow Eureka client communication
            "/actuator/**",    // Allow actuator endpoints (configure exposure carefully)
            "/actuator/health/**", // Özellikle health endpointleri için
            "/v3/api-docs/**", // OpenAPI docs
            "/swagger-ui/**",  // Swagger UI
            "/webjars/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Check if the path is public
        boolean isPublic = publicPaths.stream().anyMatch(p -> pathMatcher.match(p, path));
        if (isPublic) {
            log.debug("Path {} is public, skipping authentication.", path);
            return chain.filter(exchange);
        }

        log.debug("Authenticating request for path: {}", path);

        // Check for Authorization header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.warn("Authorization header is missing for path: {}", path);
            return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = null;

        // Extract token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
             log.warn("Authorization header is not Bearer type for path: {}", path);
             return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
        }

        // Validate token
        if (!jwtUtils.validateJwtToken(token)) {
            log.warn("Invalid JWT token received for path: {}", path);
            return onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
        }

        // Token is valid, proceed
        try {
            Claims claims = jwtUtils.getAllClaimsFromToken(token);
            String username = claims.getSubject();
            log.debug("JWT validated for user: {}, path: {}", username, path);

            // Optional: Add user info to request headers for downstream services
            // ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            //         .header("X-User-Name", username)
            //         // Add roles or other claims as needed
            //         // .header("X-User-Roles", claims.get("roles").toString())
            //         .build();
            // return chain.filter(exchange.mutate().request(mutatedRequest).build());

            return chain.filter(exchange);

        } catch (Exception e) {
            log.error("Error processing JWT or modifying request for path {}: {}", path, e.getMessage());
            return onError(exchange, "Error processing authentication token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        // Optionally set response body with error message
        // response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        // byte[] bytes = String.format("{\"error\": \"%s\"}", err).getBytes(StandardCharsets.UTF_8);
        // DataBuffer buffer = response.bufferFactory().wrap(bytes);
        // return response.writeWith(Mono.just(buffer));
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        // Ensure this filter runs before most other filters, especially routing filters
        return -100; // High precedence
    }
} 