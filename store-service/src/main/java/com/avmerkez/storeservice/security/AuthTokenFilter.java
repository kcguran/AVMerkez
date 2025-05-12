package com.avmerkez.storeservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Değiştirildi: UserDetailsServiceImpl kullanılacak

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = jwtUtils.getJwtFromCookies(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                UserDetails userDetails = userDetailsService.loadUserDetailsFromToken(jwt);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            // Güvenlik ihlali loglandı, ancak isteğin devam etmesine izin veriyoruz ki
            // @ControllerAdvice veya SecurityConfig içindeki .exceptionHandling() bunu yakalayabilsin
            // veya public endpoint ise erişilebilsin.
            // Eğer burada response.sendError atılırsa, zincir kırılır.
        }

        filterChain.doFilter(request, response);
    }

    // parseJwt metodu JwtUtils sınıfına taşındı, buradan kaldırılabilir veya JwtUtils'teki kullanılabilir.
    // Şimdilik JwtUtils.parseJwt() kullanıldığı için burada ayrıca olmasına gerek yok.
} 