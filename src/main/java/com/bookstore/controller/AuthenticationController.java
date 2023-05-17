package com.bookstore.controller;

import com.bookstore.controller.api.AuthenticationRequest;
import com.bookstore.controller.api.AuthenticationResponse;
import com.bookstore.controller.api.LogoutRequest;
import com.bookstore.controller.api.RegisterRequest;
import com.bookstore.exception.UserExistsException;
import com.bookstore.service.AuthenticationService;
import com.bookstore.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bookstore.auth.JwtAuthenticationFilter.BEARER_TOKEN;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private static final String COOKIE_NAME_REFRESH = "refresh";
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        log.info("Registering user {}.", request.getUsername());

        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (UserExistsException e) {
            final String errorMessage = "User already exists.";
            log.info(errorMessage, e);
            return ResponseEntity.badRequest()
                    .body(AuthenticationResponse.builder()
                            .message(errorMessage)
                            .build());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        log.info("Authenticating user {}.", request.getUsername());

        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);

        final Cookie cookie = new Cookie(COOKIE_NAME_REFRESH, authenticationResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        //add cookie to response
        response.addCookie(cookie);

        log.info("Returning refresh token: {}", authenticationResponse.getRefreshToken());
        return ResponseEntity.ok(authenticationResponse);
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(HttpServletRequest request) {
        log.info("Getting refresh token.");

        final AuthenticationResponse authenticationResponse = authenticationService.refresh(
                Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals(COOKIE_NAME_REFRESH))
                        .collect(Collectors.toList())
        );

        return authenticationResponse.getRefreshToken() == null ?
                ResponseEntity.badRequest().body(authenticationResponse) :
                ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(@RequestBody LogoutRequest logoutRequest,
                                                         HttpServletRequest httpServletRequest) {

        log.info("Cookies: {}", Arrays.toString(httpServletRequest.getCookies()));
        log.info("Logout request: {}", logoutRequest);

        if (httpServletRequest.getCookies() != null) {
            final List<Cookie> cookies = Arrays.stream(httpServletRequest.getCookies())
                    .filter(cookie -> cookie.getName().equals(COOKIE_NAME_REFRESH))
                    .collect(Collectors.toList());

            tokenService.saveToken(cookies.get(0).getValue());
            log.info("Access token: {}", logoutRequest.getAccessToken());
            tokenService.saveToken(logoutRequest.getAccessToken().substring(BEARER_TOKEN.length()));

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .message("User logged out successfully.")
                    .build());

        }

        return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message("Bad request.").build());
    }
}
