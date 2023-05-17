package com.bookstore.model.db.auth;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
@Builder
public class ExpiredToken {
    @Id
    private String token;
    @Indexed(expireAfterSeconds = 0)
    private Date expireAt;
    private String username;
}
