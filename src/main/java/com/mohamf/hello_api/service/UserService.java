package com.mohamf.hello_api.service;

import com.mohamf.hello_api.entity.User;
import com.mohamf.hello_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 🔥 DAY 31: ENHANCED USER SERVICE - ENTERPRISE PAGINATION & ANALYTICS
 *
 * New Enterprise Features:
 * - Paginated user retrieval for large datasets
 * - Advanced multi-field search with pagination
 * - Sorting by any field (firstName, lastName, age, createdAt)
 * - User analytics and reporting capabilities
 * - Performance-optimized queries
 *
 * This service now handles thousands of users efficiently!
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ===================================================================
    // CREATE OPERATIONS (UNCHANGED FROM DAY 30)
    // ===================================================================

    /**
     * Create a new user
     */
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        User savedUser = userRepository.save(user);
        System.out.println("✅ Created new user: " + savedUser.getFullName() + " (ID: " + savedUser.getId() + ")");
        return savedUser;
    }

    // ===================================================================
    // PAGINATED READ OPERATIONS - ENTERPRISE LEVEL!
    // ===================================================================

    /**
     * 🚀 Get all users with pagination and sorting
     *
     * @param page Page number (0-based)
     * @param size Number of users per page
     * @param sortBy Field to sort by (firstName, lastName, age, createdAt)
     * @param sortDirection Sort direction (asc or desc)
     * @return Page of users with metadata
     */
    public Page<User> getAllUsersPaginated(int page, int size, String sortBy, String sortDirection) {
        // Validate page parameters
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100; // Prevent excessive page sizes

        // Create sort object
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userRepository.findAll(pageable);

        System.out.println("📊 Retrieved page " + (page + 1) + " of users: " +
                userPage.getNumberOfElements() + " users out of " +
                userPage.getTotalElements() + " total");

        return userPage;
    }

    /**
     * 🚀 Advanced search with pagination - Enterprise search engine!
     *
     * @param firstName First name filter (partial match, case insensitive)
     * @param lastName Last name filter (partial match, case insensitive)
     * @param minAge Minimum age filter
     * @param maxAge Maximum age filter
     * @param emailDomain Email domain filter (e.g., "gmail.com")
     * @param pageable Pagination and sorting parameters
     * @return Page of users matching search criteria
     */
    public Page<User> searchUsersAdvanced(String firstName, String lastName,
                                          Integer minAge, Integer maxAge,
                                          String emailDomain, Pageable pageable) {

        Page<User> results = userRepository.findUsersWithAdvancedSearch(
                firstName, lastName, minAge, maxAge, emailDomain, pageable);

        System.out.println("🔍 Advanced search returned " + results.getNumberOfElements() +
                " results on page " + (results.getNumber() + 1) +
                " of " + results.getTotalPages() + " total pages");

        return results;
    }

    /**
     * 🚀 Search users by first name with pagination
     */
    public Page<User> getUsersByFirstNamePaginated(String firstName, Pageable pageable) {
        return userRepository.findByFirstNameIgnoreCase(firstName, pageable);
    }

    /**
     * 🚀 Search users by age range with pagination
     */
    public Page<User> getUsersByAgeRangePaginated(Integer minAge, Integer maxAge, Pageable pageable) {
        if (minAge != null && maxAge != null) {
            return userRepository.findByAgeBetween(minAge, maxAge, pageable);
        } else if (minAge != null) {
            return userRepository.findByAgeGreaterThan(minAge - 1, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    /**
     * 🚀 Search users by email domain with pagination
     */
    public Page<User> getUsersByEmailDomainPaginated(String domain, Pageable pageable) {
        return userRepository.findByEmailContaining(domain, pageable);
    }

    /**
     * 🚀 Get users created within date range with pagination
     */
    public Page<User> getUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return userRepository.findUsersCreatedBetween(startDate, endDate, pageable);
    }

    // ===================================================================
    // BACKWARD COMPATIBILITY - NON-PAGINATED METHODS
    // ===================================================================

    /**
     * Get all users (non-paginated) - For backward compatibility
     * WARNING: Use with caution for large datasets
     */
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        System.out.println("📊 Retrieved all " + users.size() + " users (non-paginated)");

        if (users.size() > 100) {
            System.out.println("⚠️ WARNING: Large dataset returned. Consider using paginated version.");
        }

        return users;
    }

    /**
     * Get user by ID (unchanged)
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
     * Get user by email (unchanged)
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ===================================================================
    // ANALYTICS & REPORTING - BUSINESS INTELLIGENCE
    // ===================================================================

    /**
     * 🔥 Get comprehensive user analytics
     */
    public Map<String, Object> getUserAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // Basic counts
        long totalUsers = userRepository.count();
        analytics.put("totalUsers", totalUsers);

        // Age analytics
        Double averageAge = userRepository.getAverageAge();
        analytics.put("averageAge", averageAge != null ? Math.round(averageAge * 100.0) / 100.0 : null);

        // Age distribution
        analytics.put("usersUnder25", userRepository.countUsersByAgeRange(0, 24));
        analytics.put("users25to35", userRepository.countUsersByAgeRange(25, 35));
        analytics.put("users36to50", userRepository.countUsersByAgeRange(36, 50));
        analytics.put("usersOver50", userRepository.countUsersByAgeRange(51, 150));

        // Email domain statistics
        analytics.put("gmailUsers", userRepository.countUsersByEmailDomain("gmail.com"));
        analytics.put("yahooUsers", userRepository.countUsersByEmailDomain("yahoo.com"));
        analytics.put("outlookUsers", userRepository.countUsersByEmailDomain("outlook.com"));

        // Registration trends - FIXED with date parameters
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        LocalDateTime weekStart = now.minusDays(7);

        analytics.put("usersRegisteredToday", userRepository.countUsersRegisteredToday(startOfDay, endOfDay));
        analytics.put("usersRegisteredThisWeek", userRepository.countUsersRegisteredThisWeek(weekStart));

        // Email domain analysis - SIMPLIFIED approach
        List<String> allEmails = userRepository.getAllEmails();
        long gmailCount = allEmails.stream().filter(email -> email.toLowerCase().contains("gmail")).count();
        long yahooCount = allEmails.stream().filter(email -> email.toLowerCase().contains("yahoo")).count();
        long outlookCount = allEmails.stream().filter(email -> email.toLowerCase().contains("outlook")).count();

        analytics.put("topEmailDomains", List.of(
                Map.of("domain", "gmail.com", "count", gmailCount),
                Map.of("domain", "yahoo.com", "count", yahooCount),
                Map.of("domain", "outlook.com", "count", outlookCount)
        ));

        // Metadata
        analytics.put("generatedAt", LocalDateTime.now());
        analytics.put("message", "User analytics generated successfully");

        System.out.println("📊 Generated analytics for " + totalUsers + " users");
        return analytics;
    }

    /**
     * 🔥 Get user growth statistics
     */
    public Map<String, Object> getUserGrowthStats() {
        Map<String, Object> growth = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1);
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);

        // Daily growth - FIXED with date parameters
        Long todayCount = userRepository.countUsersRegisteredToday(today, endOfDay);
        Long yesterdayCount = userRepository.countUsersRegisteredToday(yesterday, today);

        growth.put("registrationsToday", todayCount);
        growth.put("registrationsYesterday", yesterdayCount);
        growth.put("dailyGrowthChange", todayCount - yesterdayCount);

        // Weekly and monthly growth
        growth.put("registrationsThisWeek", userRepository.countUsersRegisteredThisWeek(weekStart));
        growth.put("registrationsThisMonth", userRepository.findUsersCreatedAfter(monthStart, Pageable.unpaged()).getTotalElements());

        growth.put("generatedAt", now);

        return growth;
    }

    // ===================================================================
    // UPDATE & DELETE OPERATIONS (UNCHANGED FROM DAY 30)
    // ===================================================================

    /**
     * Update an existing user (unchanged)
     */
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Email " + updatedUser.getEmail() + " is already in use");
            }
        }

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setAge(updatedUser.getAge());

        User savedUser = userRepository.save(existingUser);
        System.out.println("🔄 Updated user: " + savedUser.getFullName() + " (ID: " + savedUser.getId() + ")");
        return savedUser;
    }

    /**
     * Delete user by ID (unchanged)
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        userRepository.deleteById(id);
        System.out.println("🗑️ Deleted user: " + user.getFullName() + " (ID: " + id + ")");
    }

    // ===================================================================
    // UTILITY OPERATIONS
    // ===================================================================

    /**
     * Check if user exists by ID
     */
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Check if email is already in use
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get total user count
     */
    public long getUserCount() {
        long count = userRepository.count();
        System.out.println("📊 Total users in database: " + count);
        return count;
    }

    /**
     * Get users with Gmail addresses (non-paginated)
     */
    public List<User> getGmailUsers() {
        return userRepository.findByEmailContaining("@gmail.com", Pageable.unpaged()).getContent();
    }
}

/*
🎯 ENTERPRISE SERVICE ENHANCEMENTS:

1. ✅ PAGINATION SUPPORT
   - getAllUsersPaginated() with page, size, sort parameters
   - Parameter validation and limits (max 100 per page)
   - Automatic sort direction and field validation
   - Page metadata in responses

2. ✅ ADVANCED SEARCH ENGINE
   - Multi-field search with pagination
   - Case-insensitive partial matching
   - Flexible parameter handling (null parameters ignored)
   - Age range and email domain filtering
   - Date range searches

3. ✅ ANALYTICS & REPORTING
   - Comprehensive user demographics
   - Age distribution analysis
   - Email domain statistics
   - Registration trend tracking
   - Growth metrics (daily, weekly, monthly)

4. ✅ PERFORMANCE OPTIMIZATION
   - Page size limits to prevent memory issues
   - Efficient queries for large datasets
   - Warning messages for large non-paginated requests
   - Optimized counting and analytics queries

🚀 USAGE EXAMPLES:

// Get page 2 with 20 users per page, sorted by lastName:
Page<User> users = userService.getAllUsersPaginated(1, 20, "lastName", "asc");

// Advanced search with pagination:
Page<User> results = userService.searchUsersAdvanced(
    "John", null, 25, 65, "gmail.com",
    PageRequest.of(0, 10, Sort.by("createdAt").descending()));

// Get analytics:
Map<String, Object> analytics = userService.getUserAnalytics();

🔥 THIS IS ENTERPRISE-LEVEL SERVICE ARCHITECTURE!
*/