package com.mohamf.hello_api.service;

import com.mohamf.hello_api.entity.User;
import com.mohamf.hello_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 🔥 DAY 30: USER SERVICE - BUSINESS LOGIC LAYER
 *
 * This service class handles all business logic for User operations.
 * It sits between the Controller (REST API) and Repository (Database).
 *
 * Benefits of Service Layer:
 * - Keeps controllers clean and focused on HTTP concerns
 * - Centralizes business logic and validation
 * - Makes code reusable across different controllers
 * - Easier to test business logic separately
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructor injection of UserRepository
     * Spring automatically provides the repository implementation
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===================================================================
    // CREATE OPERATIONS
    // ===================================================================

    /**
     * Create a new user
     *
     * @param user User object to create
     * @return Created user with generated ID
     * @throws IllegalArgumentException if email already exists
     */
    public User createUser(User user) {
        // Business rule: Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        // Save user to database (JPA automatically generates ID)
        User savedUser = userRepository.save(user);

        System.out.println("✅ Created new user: " + savedUser.getFullName() + " (ID: " + savedUser.getId() + ")");
        return savedUser;
    }

    // ===================================================================
    // READ OPERATIONS
    // ===================================================================

    /**
     * Get all users from database
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        System.out.println("📊 Retrieved " + users.size() + " users from database");
        return users;
    }

    /**
     * Get user by ID
     *
     * @param id User ID to search for
     * @return User if found, empty Optional if not found
     */
    public Optional<User> getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            System.out.println("✅ Found user: " + user.get().getFullName());
        } else {
            System.out.println("❌ User not found with ID: " + id);
        }
        return user;
    }

    /**
     * Get user by email address
     *
     * @param email Email to search for
     * @return User if found, empty Optional if not found
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Search users by first name (case insensitive)
     *
     * @param firstName First name to search for
     * @return List of users matching the first name
     */
    public List<User> getUsersByFirstName(String firstName) {
        return userRepository.findByFirstNameIgnoreCase(firstName);
    }

    /**
     * Get users by age range
     *
     * @param minAge Minimum age
     * @param maxAge Maximum age
     * @return List of users in the age range
     */
    public List<User> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        if (minAge != null && maxAge != null) {
            return userRepository.findByAgeBetween(minAge, maxAge);
        } else if (minAge != null) {
            return userRepository.findByAgeGreaterThan(minAge - 1);
        } else {
            return getAllUsers();
        }
    }

    // ===================================================================
    // UPDATE OPERATIONS
    // ===================================================================

    /**
     * Update an existing user
     *
     * @param id User ID to update
     * @param updatedUser User object with updated information
     * @return Updated user
     * @throws RuntimeException if user not found or email conflict
     */
    public User updateUser(Long id, User updatedUser) {
        // Check if user exists
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Business rule: Check email uniqueness (if email is being changed)
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Email " + updatedUser.getEmail() + " is already in use");
            }
        }

        // Update fields (keeping the original ID and createdAt)
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setAge(updatedUser.getAge());
        // updatedAt will be automatically set by @PreUpdate

        User savedUser = userRepository.save(existingUser);
        System.out.println("🔄 Updated user: " + savedUser.getFullName() + " (ID: " + savedUser.getId() + ")");
        return savedUser;
    }

    // ===================================================================
    // DELETE OPERATIONS
    // ===================================================================

    /**
     * Delete user by ID
     *
     * @param id User ID to delete
     * @throws RuntimeException if user not found
     */
    public void deleteUser(Long id) {
        // Check if user exists
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        userRepository.deleteById(id);
        System.out.println("🗑️ Deleted user: " + user.getFullName() + " (ID: " + id + ")");
    }

    /**
     * Delete all users (be careful with this!)
     */
    public void deleteAllUsers() {
        long count = userRepository.count();
        userRepository.deleteAll();
        System.out.println("🗑️ Deleted all " + count + " users from database");
    }

    // ===================================================================
    // UTILITY OPERATIONS
    // ===================================================================

    /**
     * Check if user exists by ID
     *
     * @param id User ID to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Check if email is already in use
     *
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get total user count
     *
     * @return Number of users in database
     */
    public long getUserCount() {
        long count = userRepository.count();
        System.out.println("📊 Total users in database: " + count);
        return count;
    }

    /**
     * Get users with Gmail addresses
     *
     * @return List of users with Gmail emails
     */
    public List<User> getGmailUsers() {
        return userRepository.findByEmailDomain("@gmail.com");
    }
}

/*
🎯 SERVICE LAYER BENEFITS:

1. ✅ BUSINESS LOGIC CENTRALIZATION
   - Email uniqueness validation
   - User existence checking
   - Data consistency rules
   - Error handling with meaningful messages

2. ✅ SEPARATION OF CONCERNS
   - Controller handles HTTP requests/responses
   - Service handles business logic
   - Repository handles database operations

3. ✅ REUSABILITY
   - Service methods can be used by multiple controllers
   - Easy to test business logic separately
   - Clear API for user operations

4. ✅ ERROR HANDLING
   - Meaningful exception messages
   - Business rule validation
   - Consistent error responses

🚀 WHAT THIS ENABLES:
- Complete user lifecycle management
- Email uniqueness enforcement
- Flexible user searching and filtering
- Safe update operations
- Comprehensive validation

🔥 THIS IS ENTERPRISE-LEVEL ARCHITECTURE!
*/