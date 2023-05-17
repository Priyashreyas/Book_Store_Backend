package com.bookstore.controller;

import com.bookstore.controller.api.APIUser;
import com.bookstore.controller.api.UserRequest;
import com.bookstore.controller.api.UserResponse;
import com.bookstore.model.db.auth.User;
import com.bookstore.service.UserService;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getAllUsers() {
        List<User> users = userService.getAllUsers();
        log.info("Returning {} users.", users.size());
        return ResponseEntity.ok(UserResponse.builder()
                .users(users.stream()
                        .map(APIUser::new)
                        .collect(Collectors.toList()))
                .build());
    }

    @GetMapping
    @RequestMapping("/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
        log.info("Returning user with username {}.", username);

        Optional<User> user = userService.getUser(username);

        return user.map(value -> ResponseEntity.ok(
                        UserResponse.builder()
                                .users(ImmutableList.of(new APIUser(value)))
                                .build()))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(UserResponse.builder()
                                .message(String.format("User with username %s does not exist.", username))
                                .build()));

    }

    @PreAuthorize("hasRole(T(com.bookstore.model.db.auth.Role).ROLE_ADMIN)")
    @PostMapping
    public ResponseEntity<UserResponse> saveBook(@RequestBody UserRequest request) {
        log.debug("Request: {}", request);
        final APIUser user = request.getUser();
        log.debug("User = {}", user);
        if (user == null) {
            log.info("User is null.");
            return ResponseEntity.badRequest().body(UserResponse.builder()
                    .message("User is empty.")
                    .build());
        }
        log.info("Saving user with username {}.", user.getUsername());

        if (!userService.saveUser(request.getUser())) {
            ResponseEntity.internalServerError()
                    .body(UserResponse.builder()
                            .message(String.format("Could not save the user with username %s.", user.getUsername()))
                            .build());
        }

        return ResponseEntity.ok()
                .body(UserResponse.builder()
                        .message(String.format("User with username %s saved successfully.", user.getUsername()))
                        .build());
    }

    @PreAuthorize("hasRole(T(com.bookstore.model.db.auth.Role).ROLE_ADMIN)")
    @DeleteMapping
    public ResponseEntity<UserResponse> deleteUser(@RequestParam(value = "username") Optional<String> username) {
        log.debug("Username = {}", username.orElse("null"));
        if (!username.isPresent()) {
            log.info("Username is null.");
            return ResponseEntity.badRequest().body(UserResponse.builder()
                    .message("Username is empty.")
                    .build());
        }
        log.info("Deleting user with username {}.", username.get());

        try {
            userService.deleteUser(username.get());
            return ResponseEntity.ok()
                    .body(UserResponse.builder()
                            .message(String.format("User with username %s deleted successfully.", username.get()))
                            .build());
        } catch (RuntimeException e) {
            log.error("Unexpected exception: ", e);
        }

        return ResponseEntity.internalServerError()
                .body(UserResponse.builder()
                        .message(String.format("Could not delete the user with username %s.", username.get()))
                        .build());
    }
}
