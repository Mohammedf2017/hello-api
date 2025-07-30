package com.mohamf.hello_api.controller;

import com.mohamf.hello_api.dto.ApiResponse;
import com.mohamf.hello_api.entity.User;
import com.mohamf.hello_api.service.ApiMonitoringService;
import com.mohamf.hello_api.service.UserService;
import com.mohamf.hello_api.validation.UserValidator;
import com.mohamf.hello_api.validation.UserValidator.ValidationResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Enterprise User Controller with Performance Monitoring
 * Features:
 * - Real-time performance tracking
 * - Request/response analytics
 * - Enterprise validation
 * - Professional response format
 * - Performance insights
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private ApiMonitoringService monitoringService;

    /**
     * Create User with Enterprise Validation and Performance Monitoring
     */
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        boolean success = false;

        try {
            // Step 1: Check basic Spring validation
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());

                ApiResponse<User> response = ApiResponse.<User>validationError(
                                "Basic validation failed", errors)
                        .withRequestId(requestId)
                        .withExecutionTime(System.currentTimeMillis() - startTime);

                return ResponseEntity.badRequest().body(response);
            }

            // Step 2: Enterprise validation
            ValidationResult validationResult = userValidator.validateUser(user);

            if (!validationResult.isValid()) {
                ApiResponse<User> response = ApiResponse.<User>validationErrorWithWarnings(
                                "Enterprise validation failed",
                                validationResult.getErrors(),
                                validationResult.getWarnings())
                        .withRequestId(requestId)
                        .withExecutionTime(System.currentTimeMillis() - startTime);

                return ResponseEntity.badRequest().body(response);
            }

            // Step 3: Create user
            User savedUser = userService.createUser(user);
            success = true;

            // Step 4: Build success response
            String message = "User created successfully with enterprise validation";
            ApiResponse<User> response;

            if (!validationResult.getWarnings().isEmpty()) {
                response = ApiResponse.successWithValidation(savedUser, message, validationResult.getWarnings());
            } else {
                response = ApiResponse.success(savedUser, message);
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response
                            .withRequestId(requestId)
                            .withExecutionTime(System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            ApiResponse<User> response = ApiResponse.<User>error(
                            "Failed to create user: " + e.getMessage(),
                            "INTERNAL_ERROR")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Record performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            monitoringService.recordRequest("/api/users", "POST", executionTime, success, requestId);
        }
    }

    /**
     * Update User with Performance Monitoring
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @Valid @RequestBody User user, BindingResult bindingResult) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        boolean success = false;

        try {
            // Check if user exists
            Optional<User> existingUserOpt = userService.getUserById(id);
            if (existingUserOpt.isEmpty()) {
                ApiResponse<User> response = ApiResponse.<User>error(
                                "User not found with id: " + id,
                                "USER_NOT_FOUND")
                        .withRequestId(requestId)
                        .withExecutionTime(System.currentTimeMillis() - startTime);

                return ResponseEntity.notFound().build();
            }

            User existingUser = existingUserOpt.get();

            // Basic validation
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());

                ApiResponse<User> response = ApiResponse.<User>validationError(
                                "Basic validation failed", errors)
                        .withRequestId(requestId)
                        .withExecutionTime(System.currentTimeMillis() - startTime);

                return ResponseEntity.badRequest().body(response);
            }

            // Enterprise validation with update-specific checks
            ValidationResult validationResult = userValidator.validateUserUpdate(existingUser, user);

            if (!validationResult.isValid()) {
                ApiResponse<User> response = ApiResponse.<User>validationErrorWithWarnings(
                                "Enterprise validation failed",
                                validationResult.getErrors(),
                                validationResult.getWarnings())
                        .withRequestId(requestId)
                        .withExecutionTime(System.currentTimeMillis() - startTime);

                return ResponseEntity.badRequest().body(response);
            }

            // Update user
            User updatedUser = userService.updateUser(id, user);
            success = true;

            // Build success response
            String message = "User updated successfully with enterprise validation";
            ApiResponse<User> response;

            if (!validationResult.getWarnings().isEmpty()) {
                response = ApiResponse.successWithValidation(updatedUser, message, validationResult.getWarnings());
            } else {
                response = ApiResponse.success(updatedUser, message);
            }

            return ResponseEntity.ok(response
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            ApiResponse<User> response = ApiResponse.<User>error(
                            "Failed to update user: " + e.getMessage(),
                            "INTERNAL_ERROR")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Record performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            monitoringService.recordRequest("/api/users/{id}", "PUT", executionTime, success, requestId);
        }
    }

    /**
     * Validation-Only Endpoint with Performance Monitoring
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Object>> validateUserData(@RequestBody User user) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        boolean success = false;

        try {
            ValidationResult validationResult = userValidator.validateUser(user);

            Object validationData = new Object() {
                public final boolean valid = validationResult.isValid();
                public final boolean meetsProfessionalStandards = validationResult.meetsProfessionalStandards();
                public final boolean emailDomainAllowed = validationResult.isEmailDomainAllowed();
                public final String summary = validationResult.toString();
                public final List<String> errors = validationResult.getErrors();
                public final List<String> warnings = validationResult.getWarnings();
            };

            String message = validationResult.isValid() ?
                    "User data passes all enterprise validation checks" :
                    "User data has validation issues";

            // For validation endpoint, always return success=true since validation operation succeeded
            ApiResponse<Object> response = ApiResponse.success(validationData, message);
            success = true;

            return ResponseEntity.ok(response
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            ApiResponse<Object> response = ApiResponse.<Object>error(
                            "Failed to validate user data: " + e.getMessage(),
                            "VALIDATION_ERROR")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Record performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            monitoringService.recordRequest("/api/users/validate", "POST", executionTime, success, requestId);
        }
    }

    /**
     * Get All Users with Performance Monitoring
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        boolean success = false;

        try {
            List<User> users = userService.getAllUsers();
            success = true;

            ApiResponse<List<User>> response = ApiResponse.success(
                            users,
                            "Users retrieved successfully (" + users.size() + " found)")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<List<User>> response = ApiResponse.<List<User>>error(
                            "Failed to retrieve users: " + e.getMessage(),
                            "DATA_ACCESS_ERROR")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Record performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            monitoringService.recordRequest("/api/users", "GET", executionTime, success, requestId);
        }
    }

    /**
     * 🚀 API Analytics Dashboard - MUST COME BEFORE /{id} mapping
     * Real-time performance monitoring and insights
     */
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalyticsDashboard() {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        boolean success = false;

        try {
            Map<String, Object> analytics = monitoringService.getAnalyticsDashboard();
            success = true;

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                            analytics,
                            "API analytics dashboard retrieved successfully")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>error(
                            "Failed to retrieve analytics: " + e.getMessage(),
                            "ANALYTICS_ERROR")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Record performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            monitoringService.recordRequest("/api/users/analytics", "GET", executionTime, success, requestId);
        }
    }

    /**
     * Enhanced Email Check with Performance Monitoring
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Object>> checkEmailAvailability(@RequestParam String email) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        boolean success = false;

        try {
            if (email == null || email.trim().isEmpty()) {
                ApiResponse<Object> response = ApiResponse.<Object>error(
                                "Email parameter is required",
                                "INVALID_PARAMETER")
                        .withRequestId(requestId)
                        .withExecutionTime(System.currentTimeMillis() - startTime);

                return ResponseEntity.badRequest().body(response);
            }

            boolean exists = userService.emailExists(email);
            boolean domainAllowed = userValidator.isEmailDomainAllowed(email);

            final String emailParam = email;
            final boolean emailExists = exists;
            final boolean emailDomainAllowed = domainAllowed;

            Object emailData = new Object() {
                public final String email = emailParam;
                public final boolean exists = emailExists;
                public final boolean available = !emailExists;
                public final boolean domainAllowed = emailDomainAllowed;
            };

            String message = exists ? "Email is already in use" : "Email is available";
            ApiResponse<Object> response = ApiResponse.success(emailData, message);

            if (!domainAllowed) {
                response = ApiResponse.successWithValidation(
                        emailData,
                        message,
                        List.of("Email domain is not allowed by enterprise policy"));
            }

            success = true;
            return ResponseEntity.ok(response
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            ApiResponse<Object> response = ApiResponse.<Object>error(
                            "Failed to check email availability: " + e.getMessage(),
                            "EMAIL_CHECK_ERROR")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Record performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            monitoringService.recordRequest("/api/users/check-email", "GET", executionTime, success, requestId);
        }
    }

    /**
     * Get User Statistics with Performance Monitoring
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getUserStats() {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        boolean success = false;

        try {
            List<User> allUsers = userService.getAllUsers();

            Object stats = new Object() {
                public final int totalUsers = allUsers.size();
                public final double averageAge = allUsers.stream()
                        .filter(u -> u.getAge() != null)
                        .mapToInt(User::getAge)
                        .average()
                        .orElse(0.0);
                public final long usersOver18 = allUsers.stream()
                        .filter(u -> u.getAge() != null && u.getAge() >= 18)
                        .count();
                public final String timestamp = java.time.LocalDateTime.now().toString();
            };

            success = true;
            ApiResponse<Object> response = ApiResponse.success(
                            stats,
                            "User statistics retrieved successfully")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Object> response = ApiResponse.<Object>error(
                            "Failed to get statistics: " + e.getMessage(),
                            "STATS_ERROR")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Record performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            monitoringService.recordRequest("/api/users/stats", "GET", executionTime, success, requestId);
        }
    }

    /**
     * Get User by ID with Performance Monitoring
     * IMPORTANT: This MUST come AFTER specific mappings like /analytics, /stats, /check-email
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        boolean success = false;

        try {
            Optional<User> user = userService.getUserById(id);

            if (user.isPresent()) {
                success = true;
                ApiResponse<User> response = ApiResponse.success(
                                user.get(),
                                "User retrieved successfully")
                        .withRequestId(requestId)
                        .withExecutionTime(System.currentTimeMillis() - startTime);

                return ResponseEntity.ok(response);
            } else {
                ApiResponse<User> response = ApiResponse.<User>error(
                                "User not found with id: " + id,
                                "USER_NOT_FOUND")
                        .withRequestId(requestId)
                        .withExecutionTime(System.currentTimeMillis() - startTime);

                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            ApiResponse<User> response = ApiResponse.<User>error(
                            "Failed to retrieve user: " + e.getMessage(),
                            "DATA_ACCESS_ERROR")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Record performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            monitoringService.recordRequest("/api/users/{id}", "GET", executionTime, success, requestId);
        }
    }

    /**
     * Delete User with Performance Monitoring
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        boolean success = false;

        try {
            Optional<User> user = userService.getUserById(id);
            if (user.isEmpty()) {
                ApiResponse<Object> response = ApiResponse.<Object>error(
                                "User not found with id: " + id,
                                "USER_NOT_FOUND")
                        .withRequestId(requestId)
                        .withExecutionTime(System.currentTimeMillis() - startTime);

                return ResponseEntity.notFound().build();
            }

            userService.deleteUser(id);
            success = true;

            ApiResponse<Object> response = ApiResponse.success(
                            null,
                            "User deleted successfully")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Object> response = ApiResponse.<Object>error(
                            "Failed to delete user: " + e.getMessage(),
                            "DATA_ACCESS_ERROR")
                    .withRequestId(requestId)
                    .withExecutionTime(System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            // Record performance metrics
            long executionTime = System.currentTimeMillis() - startTime;
            monitoringService.recordRequest("/api/users/{id}", "DELETE", executionTime, success, requestId);
        }
    }
}