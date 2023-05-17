package com.bookstore.auth;

import com.bookstore.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String BEARER_TOKEN = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER_NAME);

//        logRequestInfo(request);

        if (authHeader == null || !authHeader.startsWith(BEARER_TOKEN.trim())) {
            log.info("Auth header: {}", authHeader);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            filterChain.doFilter(request, response);
            log.info("returning without authentication.");
            return;
        }
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String jwtToken = null;
        try {
            jwtToken = authHeader.substring(BEARER_TOKEN.length());
            final String username = jwtService.extractUsername(jwtToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //User is already authenticated
                if (tokenService.isTokenExpired(jwtToken, username)) {
                    log.info("Token expired.");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
                    return;
                }
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    log.debug("Token is valid.");
                    final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Going to next filter.");
                    filterChain.doFilter(request, response);
                } else {
                    log.debug("Token is invalid.");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                }
            } else {
                log.debug("Going to next filter.");
                filterChain.doFilter(request, response);
            }
        } catch (ExpiredJwtException e) {
            log.info("Token expired.", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            return;
        } catch (MalformedJwtException e) {
            log.info("Malformed token {}.", jwtToken, e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            return;
        } catch (JwtException e) {
            log.info("Jwt Exception.", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            return;
        } catch (UsernameNotFoundException e) {
            log.info("Username not found.", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            return;
        } catch (StringIndexOutOfBoundsException e) {
            log.info("No bearer token found.", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            return;
        }
    }

    private void logRequestInfo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                try {
                    log.info("----\nCookie name: {}", cookie.getName());
                    log.info("Cookie value: {}", cookie.getValue());
                    log.info("Cookie comment: {}", cookie.getComment());
                    log.info("Cookie path: {}", cookie.getPath());
                    log.info("Cookie domain: {}", cookie.getDomain());
                    log.info("Cookie secure: {}", cookie.getSecure());
                    log.info("Cookie max age: {}", cookie.getMaxAge());
                    log.info("Cookie version: {}", cookie.getVersion());
                    log.info("Cookie class: {}----\n", cookie.getClass());
                } catch (Exception e) {
                    log.error("Error: ", e);
                }
            }
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            try {
                String headerName = headerNames.nextElement();

                log.info("Header {}", request.getHeader(headerName));
            } catch (Exception e) {
                log.error("Error: ", e);
            }
        }
    }
}
