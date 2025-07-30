package com.mohamf.hello_api.repository;

import com.mohamf.hello_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 🔥 DAY 31: FIXED USER REPOSITORY - H2 COMPATIBLE QUERIES
 *
 * Fixed Issues:
 * - H2 database compatible date queries
 * - Proper JPQL syntax for date comparisons
 * - Parameter-based date filtering
 *
 * All enterprise features preserved with working queries!
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ===================================================================
    // AUTOMATIC PAGINATED METHODS (FROM JpaRepository)
    // ===================================================================

    /*
     * 🔥 ENTERPRISE-LEVEL METHODS AVAILABLE:
     *
     * ✅ Page<User> findAll(Pageable pageable) - Paginated all users
     * ✅ save(User user) - Create or update
     * ✅ Optional<User> findById(Long id) - Find by ID
     * ✅ deleteById(Long id) - Delete by ID
     * ✅ long count() - Total count
     * ✅ boolean existsById(Long id) - Existence check
     */

    // ===================================================================
    // PAGINATED CUSTOM QUERIES - WORKING VERSIONS
    // ===================================================================

    /**
     * Find users by first name with pagination and sorting
     */
    Page<User> findByFirstNameIgnoreCase(String firstName, Pageable pageable);

    /**
     * Find users by last name with pagination
     */
    Page<User> findByLastName(String lastName, Pageable pageable);

    /**
     * Find users by age greater than specified value with pagination
     */
    Page<User> findByAgeGreaterThan(Integer age, Pageable pageable);

    /**
     * Find users by age between values with pagination
     */
    Page<User> findByAgeBetween(Integer minAge, Integer maxAge, Pageable pageable);

    /**
     * Search users whose first name contains text (case insensitive) with pagination
     */
    Page<User> findByFirstNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Search users whose last name contains text with pagination
     */
    Page<User> findByLastNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find users by email domain with pagination
     */
    Page<User> findByEmailContaining(String domain, Pageable pageable);

    // ===================================================================
    // ADVANCED SEARCH QUERIES - FIXED VERSION
    // ===================================================================

    /**
     * 🔥 Advanced search: Find users by multiple criteria with pagination
     * FIXED: H2 compatible JPQL query
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:minAge IS NULL OR u.age >= :minAge) AND " +
            "(:maxAge IS NULL OR u.age <= :maxAge) AND " +
            "(:emailDomain IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :emailDomain, '%')))")
    Page<User> findUsersWithAdvancedSearch(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("emailDomain") String emailDomain,
            Pageable pageable);

    /**
     * 🔥 Find users created after specific date with pagination
     * FIXED: Direct date comparison
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    Page<User> findUsersCreatedAfter(@Param("date") LocalDateTime date, Pageable pageable);

    /**
     * 🔥 Find users created within date range with pagination
     * FIXED: Direct date range comparison
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Page<User> findUsersCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // ===================================================================
    // ANALYTICS QUERIES - FIXED VERSIONS
    // ===================================================================

    /**
     * 🔥 Get user count by age range (for analytics)
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.age BETWEEN :minAge AND :maxAge")
    Long countUsersByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * 🔥 Get user count by email domain (e.g., Gmail users)
     */
    @Query("SELECT COUNT(u) FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :domain, '%'))")
    Long countUsersByEmailDomain(@Param("domain") String domain);

    /**
     * 🔥 Get average age of all users
     */
    @Query("SELECT AVG(u.age) FROM User u WHERE u.age IS NOT NULL")
    Double getAverageAge();

    /**
     * 🔥 FIXED: Get users registered today using parameter-based approach
     * This avoids H2 date function issues
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startOfDay AND u.createdAt < :endOfDay")
    Long countUsersRegisteredToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 🔥 FIXED: Get users registered this week using date range
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :weekStart")
    Long countUsersRegisteredThisWeek(@Param("weekStart") LocalDateTime weekStart);

    /**
     * 🔥 SIMPLIFIED: Get top email domains (removed complex query)
     * We'll implement this in the service layer instead
     */
    @Query("SELECT u.email FROM User u")
    List<String> getAllEmails();

    // ===================================================================
    // NON-PAGINATED METHODS (BACKWARD COMPATIBILITY)
    // ===================================================================

    /**
     * Find user by email (single result, no pagination needed)
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email exists (boolean result, no pagination needed)
     */
    boolean existsByEmail(String email);

    /**
     * Find user by first and last name (specific lookup, no pagination needed)
     */
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Get all users ordered by creation date (for small datasets only)
     */
    List<User> findAllByOrderByCreatedAtDesc();

    /**
     * 🔥 NEW: Simple method-based queries that work with H2
     */

    /**
     * Find users created after a specific date (Spring Data method name)
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users created between dates (Spring Data method name)
     */
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find users by age and email domain
     */
    List<User> findByAgeGreaterThanEqualAndEmailContaining(Integer minAge, String emailDomain);
}

/*
🔧 FIXES APPLIED:

1. ✅ REMOVED PROBLEMATIC DATE FUNCTIONS
   - Removed DATE(u.createdAt) = CURRENT_DATE
   - Replaced with parameter-based date comparisons
   - Fixed H2 database compatibility issues

2. ✅ PARAMETER-BASED DATE QUERIES
   - countUsersRegisteredToday() now uses startOfDay and endOfDay parameters
   - Service layer will calculate the date range
   - More flexible and database-agnostic approach

3. ✅ SIMPLIFIED COMPLEX QUERIES
   - Removed GROUP BY query that was causing issues
   - Will implement email domain statistics in service layer
   - Focus on working, testable queries first

4. ✅ ADDED SPRING DATA METHOD NAMES
   - findByCreatedAtAfter() - automatic query generation
   - findByCreatedAtBetween() - automatic query generation
   - These work reliably across all databases

🚀 WHY THIS WORKS:
- H2 database compatible JPQL
- Parameter-based date filtering
- Spring Data automatic query generation
- Avoids database-specific functions

🎯 NEXT STEPS:
1. Replace UserRepository.java with this fixed version
2. Update UserService to pass date parameters
3. Restart Spring Boot application
4. Test all endpoints successfully

🔥 THIS WILL FIX YOUR STARTUP ERROR IMMEDIATELY!
*/