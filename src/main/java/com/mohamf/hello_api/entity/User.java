package com.mohamf.hello_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 🔥 DAY 30: USER ENTITY - YOUR FIRST DATABASE TABLE!
 *
 * This class will automatically create a 'users' table in your H2 database
 * when you restart your Spring Boot application!
 *
 * JPA Annotations Explained:
 * @Entity - Tells JPA this is a database table
 * @Table - Specifies the table name
 * @Id - Marks the primary key field
 * @GeneratedValue - Auto-generates ID values
 * @Column - Customizes column properties
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Primary Key - Auto-generated ID
     * JPA will automatically create sequence for this
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * User's first name
     * - Cannot be null or empty (@NotBlank)
     * - Must be between 2-50 characters
     * - Maps to 'first_name' column
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2-50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * User's last name
     * - Cannot be null or empty
     * - Must be between 2-50 characters
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2-50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * User's email address
     * - Must be valid email format
     * - Cannot be null or empty
     * - Must be unique in database
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * User's age (optional field)
     */
    @Column(name = "age")
    private Integer age;

    /**
     * When the user was created
     * - Automatically set when entity is first saved
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * When the user was last updated
     * - Automatically updated on every save
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===================================================================
    // CONSTRUCTORS
    // ===================================================================

    /**
     * Default constructor (required by JPA)
     */
    public User() {
    }

    /**
     * Constructor for creating new users
     */
    public User(String firstName, String lastName, String email, Integer age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===================================================================
    // JPA LIFECYCLE METHODS
    // ===================================================================

    /**
     * Called before entity is saved to database
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Called before entity is updated in database
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===================================================================
    // GETTERS AND SETTERS
    // ===================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ===================================================================
    // UTILITY METHODS
    // ===================================================================

    /**
     * Get full name (firstName + lastName)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * String representation of User
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /**
     * Equals method (based on email uniqueness)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return email != null && email.equals(user.email);
    }

    /**
     * HashCode method (based on email)
     */
    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}

/*
🎯 WHAT THIS ENTITY DOES:

1. ✅ AUTOMATIC TABLE CREATION
   - JPA will create 'users' table with all columns
   - Primary key with auto-increment ID
   - Proper data types and constraints

2. ✅ VALIDATION RULES
   - Email format validation
   - Required field validation
   - Size constraints
   - Unique email constraint

3. ✅ AUTOMATIC TIMESTAMPS
   - createdAt set when user is first saved
   - updatedAt set every time user is modified

4. ✅ BUSINESS LOGIC
   - Full name calculation
   - Proper toString, equals, hashCode methods

🚀 AFTER RESTART, YOU'LL SEE:
- New 'users' table in H2 console
- Automatic SQL CREATE TABLE statements in console
- Ready for CRUD operations!

🔥 THIS IS THE FOUNDATION FOR ALL DATABASE OPERATIONS!
*/