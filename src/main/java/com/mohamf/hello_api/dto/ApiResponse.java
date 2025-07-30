package com.mohamf.hello_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized API Response Format for Enterprise Applications
 * Provides consistent response structure across all endpoints
 *
 * Features:
 * - Consistent success/error handling
 * - Standardized metadata
 * - Professional error codes
 * - Request tracking
 * - Performance metrics
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    // Core Response Fields
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    // Error Information
    @JsonProperty("error")
    private ErrorDetails error;

    // Metadata
    @JsonProperty("meta")
    private ResponseMetadata meta;

    // Validation Information
    @JsonProperty("validation")
    private ValidationInfo validation;

    // Constructors
    private ApiResponse() {
        this.meta = new ResponseMetadata();
    }

    // Success Response Builders
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = "Request completed successfully";
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> successWithValidation(T data, String message,
                                                           List<String> warnings) {
        ApiResponse<T> response = success(data, message);
        response.validation = new ValidationInfo();
        response.validation.warnings = warnings;
        response.validation.hasWarnings = !warnings.isEmpty();
        return response;
    }

    // Error Response Builders
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.error = new ErrorDetails();
        response.error.code = errorCode;
        response.error.message = message;
        return response;
    }

    public static <T> ApiResponse<T> validationError(String message, List<String> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.error = new ErrorDetails();
        response.error.code = "VALIDATION_FAILED";
        response.error.message = message;
        response.validation = new ValidationInfo();
        response.validation.errors = errors;
        response.validation.hasErrors = !errors.isEmpty();
        return response;
    }

    public static <T> ApiResponse<T> validationErrorWithWarnings(String message,
                                                                 List<String> errors,
                                                                 List<String> warnings) {
        ApiResponse<T> response = validationError(message, errors);
        response.validation.warnings = warnings;
        response.validation.hasWarnings = !warnings.isEmpty();
        return response;
    }

    // Method Chaining for Additional Information
    public ApiResponse<T> withRequestId(String requestId) {
        this.meta.requestId = requestId;
        return this;
    }

    public ApiResponse<T> withExecutionTime(long executionTimeMs) {
        this.meta.executionTimeMs = executionTimeMs;
        return this;
    }

    public ApiResponse<T> withPagination(PaginationInfo pagination) {
        this.meta.pagination = pagination;
        return this;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public ErrorDetails getError() { return error; }
    public ResponseMetadata getMeta() { return meta; }
    public ValidationInfo getValidation() { return validation; }

    /**
     * Error Details for Failed Requests
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;

        @JsonProperty("details")
        private Map<String, Object> details;

        // Getters
        public String getCode() { return code; }
        public String getMessage() { return message; }
        public Map<String, Object> getDetails() { return details; }
    }

    /**
     * Response Metadata
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseMetadata {
        @JsonProperty("timestamp")
        private LocalDateTime timestamp;

        @JsonProperty("requestId")
        private String requestId;

        @JsonProperty("executionTimeMs")
        private Long executionTimeMs;

        @JsonProperty("apiVersion")
        private String apiVersion;

        @JsonProperty("pagination")
        private PaginationInfo pagination;

        public ResponseMetadata() {
            this.timestamp = LocalDateTime.now();
            this.apiVersion = "v1.0";
        }

        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getRequestId() { return requestId; }
        public Long getExecutionTimeMs() { return executionTimeMs; }
        public String getApiVersion() { return apiVersion; }
        public PaginationInfo getPagination() { return pagination; }
    }

    /**
     * Validation Information
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ValidationInfo {
        @JsonProperty("hasErrors")
        private Boolean hasErrors;

        @JsonProperty("hasWarnings")
        private Boolean hasWarnings;

        @JsonProperty("errors")
        private List<String> errors;

        @JsonProperty("warnings")
        private List<String> warnings;

        @JsonProperty("summary")
        private String summary;

        // Getters
        public Boolean getHasErrors() { return hasErrors; }
        public Boolean getHasWarnings() { return hasWarnings; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        public String getSummary() { return summary; }
    }

    /**
     * Pagination Information
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaginationInfo {
        @JsonProperty("currentPage")
        private Integer currentPage;

        @JsonProperty("totalPages")
        private Integer totalPages;

        @JsonProperty("pageSize")
        private Integer pageSize;

        @JsonProperty("totalElements")
        private Long totalElements;

        @JsonProperty("hasNext")
        private Boolean hasNext;

        @JsonProperty("hasPrevious")
        private Boolean hasPrevious;

        public PaginationInfo(int currentPage, int totalPages, int pageSize,
                              long totalElements, boolean hasNext, boolean hasPrevious) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        // Getters
        public Integer getCurrentPage() { return currentPage; }
        public Integer getTotalPages() { return totalPages; }
        public Integer getPageSize() { return pageSize; }
        public Long getTotalElements() { return totalElements; }
        public Boolean getHasNext() { return hasNext; }
        public Boolean getHasPrevious() { return hasPrevious; }
    }
}