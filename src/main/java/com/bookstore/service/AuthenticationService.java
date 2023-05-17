package com.bookstore.service;

import com.bookstore.auth.JwtService;
import com.bookstore.auth.JwtToken;
import com.bookstore.controller.api.AuthenticationRequest;
import com.bookstore.controller.api.AuthenticationResponse;
import com.bookstore.controller.api.RegisterRequest;
import com.bookstore.exception.UserExistsException;
import com.bookstore.model.db.Name;
import com.bookstore.model.db.auth.Role;
import com.bookstore.model.db.auth.User;
import com.bookstore.repo.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    public AuthenticationResponse register(RegisterRequest request) {
        final Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            throw new UserExistsException(String.format("User %s already exists.", request.getUsername()));
        }

        final User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(new Name(request.getFirstName(), request.getLastName()))
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getUsername().equals("admin") ? Role.ROLE_ADMIN : Role.ROLE_USER)
                .build();

        userRepository.save(user);

        final String accessToken = jwtService.generateToken(user, JwtToken.ACCESS_TOKEN);
        final String refreshToken = jwtService.generateToken(user, JwtToken.REFRESH_TOKEN);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().toString())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        final User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found.", request.getUsername())));

        final String accessToken = jwtService.generateToken(user, JwtToken.ACCESS_TOKEN);
        final String refreshToken = jwtService.generateToken(user, JwtToken.REFRESH_TOKEN);

        log.info("Returning tokens.");
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().toString())
                .build();
    }

    public AuthenticationResponse refresh(List<Cookie> cookies) {
        String jwtRefreshToken = null;
        try {
            jwtRefreshToken = cookies.get(0).getValue(); // TODO: Handle case of many refresh cookies
            final String username = jwtService.extractUsername(jwtRefreshToken);

            if (username != null) { // TODO: Handle clase of null username
                if (tokenService.isTokenExpired(jwtRefreshToken, username)) {
                    log.info("Token expired.");
//                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                    response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
                    return AuthenticationResponse.builder()
                            .message("Refresh token not valid.")
                            .build();
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwtRefreshToken, userDetails)) {
                    log.debug("Token is valid.");
                    final User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found.", username)));
                    final String accessToken = jwtService.generateToken(user, JwtToken.ACCESS_TOKEN);

                    log.info("Returning tokens.");
                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(jwtRefreshToken)
                            .build();
                } else {
                    log.debug("Token is invalid.");
                    //TODO: Return 400
                }
            } else {
                log.debug("Going to next filter.");
                // TODO: Return 400
            }
        } catch (ExpiredJwtException e) {
            log.info("Token expired.", e);
            // TODO: Return 400
        } catch (MalformedJwtException e) {
            log.info("Malformed token {}.", jwtRefreshToken, e);
            // TODO: Return 400
        } catch (JwtException e) {
            log.info("Jwt Exception.", e);
            // TODO: Return 400
        } catch (UsernameNotFoundException e) {
            log.info("Username not found.", e);
            // TODO: Return 400
        } catch (IllegalArgumentException e) {
            log.info("jwtRefreshToken not found.", e);
            // TODO: Return 400
        }

        return AuthenticationResponse.builder()
                .message("Refresh token not valid.")
                .build();
    }
}
