package com.oncf.gare_app.security;

import com.oncf.gare_app.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j  // Add this for logging
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Add comprehensive logging
        log.debug("=== JWT Filter Processing ===");
        log.debug("Request: {} {}", request.getMethod(), request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        log.debug("Authorization header: {}", authHeader);

        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No valid Authorization header found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        log.debug("JWT token extracted, length: {}", jwt.length());
        log.debug("JWT token preview: {}...", jwt.substring(0, Math.min(30, jwt.length())));

        try {
            username = jwtUtil.extractUsername(jwt);
            log.debug("Username extracted from JWT: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Loading user details for username: {}", username);

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                log.debug("User details loaded: {}", userDetails.getUsername());
                log.debug("User authorities: {}", userDetails.getAuthorities());

                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    log.debug("JWT token is valid, setting authentication");

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Authentication successfully set for user: {} with authorities: {}",
                            username, userDetails.getAuthorities());
                } else {
                    log.warn("JWT token validation failed for user: {}", username);
                }
            } else if (username == null) {
                log.warn("Username is null from JWT token");
            } else {
                log.debug("Authentication already exists in SecurityContext");
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: ", e);
            log.error("Exception type: {}", e.getClass().getSimpleName());
            log.error("Exception message: {}", e.getMessage());
        }

        log.debug("=== JWT Filter Completed ===");
        filterChain.doFilter(request, response);
    }
}