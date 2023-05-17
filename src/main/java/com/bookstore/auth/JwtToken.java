package com.bookstore.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
public enum JwtToken {
    ACCESS_TOKEN(Duration.of(60, ChronoUnit.MINUTES).toMillis()),
    REFRESH_TOKEN(Duration.of(24, ChronoUnit.HOURS).toMillis());

    private final long tokenValidity;
}
