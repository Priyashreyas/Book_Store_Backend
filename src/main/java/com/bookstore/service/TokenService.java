package com.bookstore.service;

import com.bookstore.auth.JwtService;
import com.bookstore.model.db.auth.ExpiredToken;
import com.bookstore.repo.TokenRepository;
import com.bookstore.repo.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TokenService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public boolean isTokenExpired(String token, String username) {
        Optional<ExpiredToken> expiredToken = tokenRepository.findExpiredTokenByToken(token);
        return expiredToken.isPresent() && expiredToken.get().getUsername().equals(username);
    }

    public boolean saveToken(String jwtToken) {
        try {
            final String username = jwtService.extractUsername(jwtToken);

            if (username != null) { // TODO: Handle clase of null username
                final ExpiredToken expiredToken = ExpiredToken.builder()
                        .token(jwtToken)
                        .expireAt(jwtService.extractExpiration(jwtToken))
                        .username(username)
                        .build();

                return tokenRepository.save(expiredToken).getToken().equals(expiredToken.getToken());
            } else {
                log.debug("Going to next filter.");
                // TODO: Return 400
            }
        } catch (ExpiredJwtException e) {
            log.info("Token expired.", e);
            // TODO: Return 400
        } catch (MalformedJwtException e) {
            log.info("Malformed token {}.", jwtToken, e);
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

        return false;
    }
}
