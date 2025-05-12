package com.avmerkez.apigateway.filter;

import com.avmerkez.apigateway.security.JwtUtilGateway;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@RefreshScope // To refresh JWT secret if changed in Config Server
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private JwtUtilGateway jwtUtil;

    // Public endpoints that do not require authentication
    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/token/refresh",
            "/api/v1/reviews/public", // Review Service public endpoints
            "/eureka", // Eureka dashboard/API might be public or secured differently
            "/swagger-ui", // Generic swagger
            "/v3/api-docs" // Generic api-docs
            // Add other specific public paths for services if needed, e.g., /user-service/v3/api-docs
    );

    // Predicate to check if the request is for an open API endpoint
    private Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints.stream()
            .noneMatch(uri -> request.getURI().getPath().contains(uri));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        logger.info("Request received: {} {}", request.getMethod(), request.getURI().getPath());

        if (isSecured.test(request)) {
            String token = jwtUtil.getJwtFromCookies(request);

            if (token == null) {
                logger.warn("JWT cookie (avm_jwt) is missing for secured endpoint: {}", request.getURI().getPath());
                return this.onError(exchange, "JWT cookie is missing in request", HttpStatus.UNAUTHORIZED);
            }
            
            logger.debug("Token extracted from cookie: {}", token);

            try {
                if (!jwtUtil.validateToken(token)) {
                    logger.warn("JWT token validation failed for path: {}", request.getURI().getPath());
                    return this.onError(exchange, "JWT Token is not valid", HttpStatus.UNAUTHORIZED);
                }

                Claims claims = jwtUtil.getAllClaimsFromToken(token);
                String username = claims.getSubject();
                List<String> roles = jwtUtil.getRolesFromToken(token);
                
                logger.debug("Token validated for user: {}, roles: {}", username, roles);

                // Add custom headers to the downstream request
                exchange.getRequest().mutate()
                        .header("X-Auth-Username", username)
                        .header("X-Auth-Roles", String.join(",", roles != null ? roles : List.of()))
                        .build();
                logger.info("Forwarding request for user {} to {}", username, request.getURI().getPath());

            } catch (Exception e) {
                logger.error("Error during JWT token validation or processing: {}", e.getMessage(), e);
                return this.onError(exchange, "Error processing JWT token", HttpStatus.UNAUTHORIZED);
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        // Optionally, write a JSON error response body
        // byte[] bytes = ("{\"error\": \"" + err + "\"}").getBytes(StandardCharsets.UTF_8);
        // DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        // return response.writeWith(Mono.just(buffer));
        logger.warn("Responding with error: Status={}, Message=\"{}\", Path={}", httpStatus, err, exchange.getRequest().getURI().getPath());
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // Execute this filter before others, e.g., LoadBalancerClientFilter (0)
    }
} 