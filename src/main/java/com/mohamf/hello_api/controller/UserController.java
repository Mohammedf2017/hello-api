package com.mohamf.hello_api.controller;

import com.mohamf.hello_api.entity.User;
import com.mohamf.hello_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 🔥 DAY 30: USER CONTROLLER - FIXED VERSION
 *
 * This controller provides 5 CRUD endpoints for User management:
 * 1. POST /api/users - Create new user
 * 2. GET /api/users - Get all users (with optional filtering)
 * 3. GET /api/users/{id} - Get specific user by ID
 * 4. PUT /api/users/{id} - Update existing user
 * 5. DELETE /api/users/{id} - Delete user
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ===================================================================
    // CREATE ENDPOINT - POST /api/users
    // ===================================================================

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User created successfully!");
            response.put("user", createdUser);
            response.put("id", createdUser.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // FIXED: Catch specific exception first
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Validation Error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "Failed to create user");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===================================================================
    // READ ENDPOINTS - GET /api/users
    // ===================================================================

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {

        List<User> users;
        String filterDescription = "all users";

        if (firstName != null && !firstName.trim().isEmpty()) {
            users = userService.getUsersByFirstName(firstName);
            filterDescription = "users with first name: " + firstName;
        } else if (minAge != null || maxAge != null) {
            users = userService.getUsersByAgeRange(minAge, maxAge);
            filterDescription = "users in age range: " +
                    (minAge != null ? minAge : "0") + " - " +
                    (maxAge != null ? maxAge : "∞");
        } else {
            users = userService.getAllUsers();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Retrieved " + filterDescription);
        response.put("count", users.size());
        response.put("users", users);
        response.put("totalUsers", userService.getUserCount());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User found");
            response.put("user", user);

            return ResponseEntity.ok(response);

        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "User Not Found");
            errorResponse.put("message", "User with ID " + id + " does not exist");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // ===================================================================
    // UPDATE ENDPOINT - PUT /api/users/{id} - FIXED VERSION
    // ===================================================================

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User updatedUser) {

        try {
            User user = userService.updateUser(id, updatedUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User updated successfully!");
            response.put("user", user);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // FIXED: Catch IllegalArgumentException BEFORE RuntimeException
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Validation Error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (RuntimeException e) {
            // FIXED: Catch RuntimeException AFTER IllegalArgumentException
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "User Not Found");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "Failed to update user");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===================================================================
    // DELETE ENDPOINT - DELETE /api/users/{id}
    // ===================================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User with ID " + id + " deleted successfully!");
            response.put("deletedId", id);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "User Not Found");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "Failed to delete user");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===================================================================
    // UTILITY ENDPOINTS
    // ===================================================================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getUserCount());
        stats.put("gmailUsers", userService.getGmailUsers().size());
        stats.put("message", "User statistics retrieved successfully");
        stats.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        boolean exists = userService.emailExists(email);

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("exists", exists);
        response.put("message", exists ? "Email is already in use" : "Email is available");

        return ResponseEntity.ok(response);
    }
}

/*
🔧 WHAT WAS FIXED:

1. ✅ EXCEPTION HANDLING ORDER
   - IllegalArgumentException caught BEFORE RuntimeException
   - More specific exceptions must be caught before general ones
   - This follows Java's exception hierarchy rules

2. ✅ PROPER CATCH BLOCK SEQUENCE
   - IllegalArgumentException (most specific)
   - RuntimeException (general runtime errors)
   - Exception (catch-all)

🎯 WHY THIS HAPPENED:
- IllegalArgumentException extends RuntimeException
- When RuntimeException is caught first, IllegalArgumentException is also caught
- Java prevents "unreachable code" situations

🚀 NOW YOUR CODE WILL COMPILE SUCCESSFULLY!
*/