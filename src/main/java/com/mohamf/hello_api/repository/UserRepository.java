package com.mohamf.hello_api.repository;

import com.mohamf.hello_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 🔥 DAY 30: USER REPOSITORY - DATABASE OPERATIONS MADE EASY!
 *
 * This interface extends JpaRepository and automatically provides:
 * - save(User) - Create or update user
 * - findById(Long) - Find user by ID
 * - findAll() - Get all users
 * - deleteById(Long) - Delete user by ID
 * - count() - Count total users
 * - existsById(Long) - Check if user exists
 *
 * Spring Data JPA automatically implements all these methods!
 * No SQL code needed - it's all generated automatically!
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ===================================================================
    // AUTOMATIC METHODS (PROVIDED BY JpaRepository)
    // ===================================================================

    /*
     * These methods are automatically available:
     *
     * ✅ save(User user) - Create or update
     * ✅ findById(Long id) - Find by primary key
     * ✅ findAll() - Get all users
     * ✅ findAll(Pageable pageable) - Get users with pagination
     * ✅ deleteById(Long id) - Delete by ID
     * ✅ delete(User user) - Delete user object
     * ✅ count() - Count total users
     * ✅ existsById(Long id) - Check if user exists
     */

    // ===================================================================
    // CUSTOM QUERY METHODS (Spring Data Magic!)
    // ===================================================================

    /**
     * Find user by email address
     * Spring Data automatically generates the SQL:
     * SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);

    /**
     * Find users by first name (case insensitive)
     * SQL: SELECT * FROM users WHERE UPPER(first_name) = UPPER(?)
     */
    List<User> findByFirstNameIgnoreCase(String firstName);

    /**
     * Find users by last name
     * SQL: SELECT * FROM users WHERE last_name = ?
     */
    List<User> findByLastName(String lastName);

    /**
     * Find users by age greater than specified value
     * SQL: SELECT * FROM users WHERE age > ?
     */
    List<User> findByAgeGreaterThan(Integer age);

    /**
     * Find users by age between two values
     * SQL: SELECT * FROM users WHERE age BETWEEN ? AND ?
     */
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    /**
     * Find users whose first name contains specified text
     * SQL: SELECT * FROM users WHERE first_name LIKE %?%
     */
    List<User> findByFirstNameContainingIgnoreCase(String name);

    /**
     * Find users by first name and last name
     * SQL: SELECT * FROM users WHERE first_name = ? AND last_name = ?
     */
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Check if user exists by email
     * SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);

    // ===================================================================
    // CUSTOM QUERIES WITH @Query ANNOTATION
    // ===================================================================

    /**
     * Custom query to find users with specific email domain
     * Example: findByEmailDomain("gmail.com") finds all Gmail users
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
    List<User> findByEmailDomain(@Param("domain") String domain);

    /**
     * Custom query to get user count by age range
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.age BETWEEN :minAge AND :maxAge")
    Long countUsersByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * Custom query to find users created after specific date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date ORDER BY u.createdAt DESC")
    List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Get all users ordered by creation date (newest first)
     */
    List<User> findAllByOrderByCreatedAtDesc();

    /**
     * Get all users ordered by last name, then first name
     */
    List<User> findAllByOrderByLastNameAscFirstNameAsc();
}

/*
🎯 SPRING DATA JPA MAGIC EXPLAINED:

1. ✅ AUTOMATIC IMPLEMENTATION
   - Spring automatically implements this interface
   - No SQL code needed for basic operations
   - All CRUD operations work immediately

2. ✅ QUERY METHOD GENERATION
   - Method names like "findByEmail" automatically generate SQL
   - Spring parses method names and creates queries
   - findByFirstNameAndLastName → WHERE first_name = ? AND last_name = ?

3. ✅ POWERFUL KEYWORDS
   - IgnoreCase → Case insensitive search
   - GreaterThan → > comparison
   - Between → BETWEEN x AND y
   - Containing → LIKE %text%
   - OrderBy → SQL ORDER BY clause

4. ✅ CUSTOM QUERIES
   - @Query annotation for complex queries
   - JPQL (Java Persistence Query Language)
   - Named parameters with @Param

🚀 WHAT YOU GET FOR FREE:
- Complete user management
- Email uniqueness checking
- Flexible search capabilities
- Sorting and ordering
- Age-based filtering
- Date-based queries

🔥 THIS IS ENTERPRISE-LEVEL DATABASE ACCESS!
*/