package com.mohamf.hello_api.validation;

import com.mohamf.hello_api.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Enterprise-level User Validator
 * IMPORTANT: This file contains ONLY the UserValidator class!
 */
@Component
public class UserValidator {

    // Professional email domains allowed in enterprise
    private static final List<String> ALLOWED_EMAIL_DOMAINS = List.of(
            "gmail.com", "outlook.com", "yahoo.com", "hotmail.com",
            "icloud.com", "protonmail.com", "company.com"
    );

    // Blocked disposable email domains
    private static final List<String> BLOCKED_EMAIL_DOMAINS = List.of(
            "tempmail.com", "10minutemail.com", "guerrillamail.com",
            "mailinator.com", "throwaway.email"
    );

    // Name validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s'-]{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Comprehensive validation result containing errors, warnings, and metadata
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;
        private final boolean meetsProfessionalStandards;
        private final boolean emailDomainAllowed;

        public ValidationResult(boolean valid, List<String> errors, List<String> warnings,
                                boolean meetsProfessionalStandards, boolean emailDomainAllowed) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
            this.meetsProfessionalStandards = meetsProfessionalStandards;
            this.emailDomainAllowed = emailDomainAllowed;
        }

        // Getters
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        public boolean meetsProfessionalStandards() { return meetsProfessionalStandards; }
        public boolean isEmailDomainAllowed() { return emailDomainAllowed; }

        @Override
        public String toString() {
            return String.format("Validation Result: %s (Errors: %d, Warnings: %d)",
                    valid ? "VALID" : "INVALID", errors.size(), warnings.size());
        }
    }

    /**
     * Main validation method - validates user data against enterprise standards
     */
    public ValidationResult validateUser(User user) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        boolean meetsProfessionalStandards = true;
        boolean emailDomainAllowed = true;

        // 1. COPPA Compliance Check (Legal requirement)
        if (user.getAge() != null && user.getAge() < 13) {
            errors.add("Users must be at least 13 years old (COPPA compliance)");
        }

        // 2. Minor Warning (Business rule)
        if (user.getAge() != null && user.getAge() < 18) {
            warnings.add("User is a minor - additional parental consent may be required");
        }

        // 3. Age Business Rules
        if (user.getAge() != null) {
            if (user.getAge() > 150) {
                errors.add("Age cannot exceed 150 years");
            }
            if (user.getAge() < 0) {
                errors.add("Age cannot be negative");
            }
        }

        // 4. Name Validation with Professional Standards
        if (user.getFirstName() != null) {
            if (!NAME_PATTERN.matcher(user.getFirstName()).matches()) {
                errors.add("First name contains invalid characters. Only letters, spaces, hyphens, and apostrophes allowed");
                meetsProfessionalStandards = false;
            }

            // Professional formatting check
            if (user.getFirstName().equals(user.getFirstName().toUpperCase())) {
                warnings.add("Consider using proper case formatting (e.g., 'John' instead of 'JOHN')");
                meetsProfessionalStandards = false;
            }

            if (user.getFirstName().length() < 2) {
                errors.add("First name must be at least 2 characters long");
            }
        }

        if (user.getLastName() != null) {
            if (!NAME_PATTERN.matcher(user.getLastName()).matches()) {
                errors.add("Last name contains invalid characters. Only letters, spaces, hyphens, and apostrophes allowed");
                meetsProfessionalStandards = false;
            }

            if (user.getLastName().length() < 2) {
                errors.add("Last name must be at least 2 characters long");
            }
        }

        // 5. Email Validation with Domain Policies
        if (user.getEmail() != null) {
            if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                errors.add("Invalid email format");
            } else {
                String domain = extractEmailDomain(user.getEmail());

                // Check for blocked domains
                if (BLOCKED_EMAIL_DOMAINS.contains(domain.toLowerCase())) {
                    errors.add("Email domain '" + domain + "' is not allowed. Please use a permanent email address");
                    emailDomainAllowed = false;
                }

                // Check for allowed domains (enterprise policy)
                if (!ALLOWED_EMAIL_DOMAINS.contains(domain.toLowerCase())) {
                    warnings.add("Email domain '" + domain + "' is not in our preferred list. Consider using a more common provider");
                }
            }
        }

        // 6. Cross-field Validation
        if (user.getFirstName() != null && user.getLastName() != null) {
            if (user.getFirstName().equalsIgnoreCase(user.getLastName())) {
                warnings.add("First name and last name are identical - verify data accuracy");
            }
        }

        boolean isValid = errors.isEmpty();
        return new ValidationResult(isValid, errors, warnings, meetsProfessionalStandards, emailDomainAllowed);
    }

    /**
     * Validation for user updates - includes change detection
     */
    public ValidationResult validateUserUpdate(User currentUser, User updateData) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // First run standard validation
        ValidationResult standardValidation = validateUser(updateData);
        errors.addAll(standardValidation.getErrors());
        warnings.addAll(standardValidation.getWarnings());

        // Additional update-specific validations
        if (currentUser != null) {
            // Email change detection
            if (!currentUser.getEmail().equals(updateData.getEmail())) {
                warnings.add("Email change detected - consider email verification");
            }

            // Suspicious age changes
            if (currentUser.getAge() != null && updateData.getAge() != null) {
                int ageDifference = Math.abs(currentUser.getAge() - updateData.getAge());
                if (ageDifference > 10) {
                    warnings.add("Large age change detected (" + ageDifference + " years) - verify accuracy");
                }
            }

            // Name consistency check
            if (!currentUser.getFirstName().equals(updateData.getFirstName()) ||
                    !currentUser.getLastName().equals(updateData.getLastName())) {
                warnings.add("Name change detected - consider requiring identity verification");
            }
        }

        boolean isValid = errors.isEmpty();
        boolean meetsProfessionalStandards = standardValidation.meetsProfessionalStandards();
        boolean emailDomainAllowed = standardValidation.isEmailDomainAllowed();

        return new ValidationResult(isValid, errors, warnings, meetsProfessionalStandards, emailDomainAllowed);
    }

    /**
     * Quick email availability check with domain validation
     */
    public boolean isEmailDomainAllowed(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }

        String domain = extractEmailDomain(email);
        return !BLOCKED_EMAIL_DOMAINS.contains(domain.toLowerCase());
    }

    /**
     * Professional standards check - used for UI warnings
     */
    public boolean meetsProfessionalStandards(User user) {
        if (user.getFirstName() != null && user.getFirstName().equals(user.getFirstName().toUpperCase())) {
            return false;
        }
        if (user.getLastName() != null && user.getLastName().equals(user.getLastName().toUpperCase())) {
            return false;
        }
        return true;
    }

    /**
     * Extract domain from email address
     */
    private String extractEmailDomain(String email) {
        int atIndex = email.lastIndexOf('@');
        return atIndex > 0 ? email.substring(atIndex + 1) : "";
    }
}