package com.bookstore.controller.api;

import com.bookstore.model.db.Name;
import com.bookstore.model.db.auth.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class APIUser {
    private String username;
    private Name displayName;
    private String email;
    private String phoneNumber;
    private String role;

    public APIUser(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.role = user.getRole().toString();
    }
}
