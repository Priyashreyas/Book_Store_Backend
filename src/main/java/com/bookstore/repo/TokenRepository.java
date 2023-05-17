package com.bookstore.repo;

import com.bookstore.model.db.auth.ExpiredToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<ExpiredToken, String> {
    Optional<ExpiredToken> findExpiredTokenByToken(String token);
}
