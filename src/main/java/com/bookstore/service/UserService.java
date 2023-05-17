package com.bookstore.service;

import com.bookstore.controller.api.APIUser;
import com.bookstore.model.db.auth.User;
import com.bookstore.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> getUser(String username) {
        return repository.findByUsername(username);
    }

    public boolean saveUser(APIUser apiUser) {
        final Optional<User> user = getUser(apiUser.getUsername());
        if (!user.isPresent()) {
            return false;
        }

        final User updatedUser = user.get();
        updatedUser.setDisplayName(apiUser.getDisplayName());
        updatedUser.setEmail(apiUser.getEmail());
        updatedUser.setPhoneNumber(apiUser.getPhoneNumber());

        repository.save(updatedUser);

        return true;
    }

    public void deleteUser(String username) {
        repository.deleteById(username);
    }
}
