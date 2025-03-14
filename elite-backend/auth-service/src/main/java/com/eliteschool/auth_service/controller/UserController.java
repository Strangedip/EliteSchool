package com.eliteschool.auth_service.controller;

import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get user by email.
     * @param email The email of the user.
     * @return ResponseEntity<User>
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get user by username.
     * @param username The username of the user.
     * @return ResponseEntity<User>
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Check if an email exists.
     * @param email The email to check.
     * @return ResponseEntity<Boolean>
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> emailExists(@PathVariable String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }

    /**
     * Check if a username exists.
     * @param username The username to check.
     * @return ResponseEntity<Boolean>
     */
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> usernameExists(@PathVariable String username) {
        return ResponseEntity.ok(userService.usernameExists(username));
    }

    /**
     * Create a new user.
     * @param user The user to save.
     * @return ResponseEntity<User>
     */
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.emailExists(user.getEmail()) || userService.usernameExists(user.getUsername())) {
            return ResponseEntity.badRequest().build(); // Avoid duplicate users
        }
        return ResponseEntity.ok(userService.saveUser(user));
    }
}
