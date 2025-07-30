package com.mohamf.hello_api.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Enterprise API Performance Monitoring Service
 *
 * Features:
 * - Real-time API performance tracking
 * - Request volume monitoring
 * - Response time analytics
 * - Error rate monitoring
 * - Endpoint popularity metrics
 * - Performance optimization insights
 */
@Service
public class ApiMonitoringService {

    // Performance Metrics Storage
    private final Map<String, EndpointMetrics> endpointMetrics = new ConcurrentHashMap<>();
    private final List<ApiRequestLog> requestLogs = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final AtomicLong totalExecutionTime = new AtomicLong(0);

    // System start time for uptime calculation
    private final LocalDateTime systemStartTime = LocalDateTime.now();

    /**
     * Record API request metrics
     */
    public void recordRequest(String endpoint, String method, long executionTimeMs,
                              boolean success, String requestId) {

        // Update global counters
        totalRequests.incrementAndGet();
        totalExecutionTime.addAndGet(executionTimeMs);

        if (!success) {
            totalErrors.incrementAndGet();
        }

        // Update endpoint-specific metrics
        String endpointKey = method + " " + endpoint;
        endpointMetrics.computeIfAbsent(endpointKey, k -> new EndpointMetrics(k))
                .recordRequest(executionTimeMs, success);

        // Log request for detailed analysis
        ApiRequestLog logEntry = new ApiRequestLog(
                requestId,
                endpoint,
                method,
                executionTimeMs,
                success,
                LocalDateTime.now()
        );

        // Keep only last 1000 requests to prevent memory issues
        synchronized (requestLogs) {
            requestLogs.add(logEntry);
            if (requestLogs.size() > 1000) {
                requestLogs.remove(0);
            }
        }
    }

    /**
     * Get comprehensive API analytics dashboard data
     */
    public Map<String, Object> getAnalyticsDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // System Overview
        dashboard.put("systemOverview", getSystemOverview());

        // Performance Metrics
        dashboard.put("performanceMetrics", getPerformanceMetrics());

        // Endpoint Analytics
        dashboard.put("endpointAnalytics", getEndpointAnalytics());

        // Recent Activity
        dashboard.put("recentActivity", getRecentActivity());

        // Performance Insights
        dashboard.put("performanceInsights", getPerformanceInsights());

        return dashboard;
    }

    /**
     * System Overview Statistics
     */
    private Map<String, Object> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();

        long totalReqs = totalRequests.get();
        long totalErrs = totalErrors.get();

        overview.put("totalRequests", totalReqs);
        overview.put("totalErrors", totalErrs);
        overview.put("successRate", totalReqs > 0 ?
                String.format("%.2f%%", ((totalReqs - totalErrs) * 100.0 / totalReqs)) : "100.00%");
        overview.put("errorRate", totalReqs > 0 ?
                String.format("%.2f%%", (totalErrs * 100.0 / totalReqs)) : "0.00%");
        overview.put("systemUptime", getFormattedUptime());
        overview.put("averageResponseTime", totalReqs > 0 ?
                (totalExecutionTime.get() / totalReqs) + "ms" : "0ms");

        return overview;
    }

    /**
     * Performance Metrics Analysis
     */
    private Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Calculate performance statistics from recent requests
        List<Long> recentResponseTimes = requestLogs.stream()
                .filter(log -> log.timestamp.isAfter(LocalDateTime.now().minusMinutes(10)))
                .map(log -> log.executionTimeMs)
                .collect(Collectors.toList());

        if (!recentResponseTimes.isEmpty()) {
            recentResponseTimes.sort(Long::compareTo);

            metrics.put("last10MinRequests", recentResponseTimes.size());
            metrics.put("averageResponseTimeLast10Min",
                    recentResponseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0) + "ms");
            metrics.put("minResponseTime", Collections.min(recentResponseTimes) + "ms");
            metrics.put("maxResponseTime", Collections.max(recentResponseTimes) + "ms");

            // Calculate percentiles
            int p50Index = (int) (recentResponseTimes.size() * 0.5);
            int p95Index = (int) (recentResponseTimes.size() * 0.95);
            int p99Index = (int) (recentResponseTimes.size() * 0.99);

            metrics.put("p50ResponseTime", recentResponseTimes.get(Math.max(0, p50Index - 1)) + "ms");
            metrics.put("p95ResponseTime", recentResponseTimes.get(Math.max(0, p95Index - 1)) + "ms");
            metrics.put("p99ResponseTime", recentResponseTimes.get(Math.max(0, p99Index - 1)) + "ms");
        } else {
            metrics.put("last10MinRequests", 0);
            metrics.put("message", "No requests in the last 10 minutes");
        }

        return metrics;
    }

    /**
     * Endpoint-specific Analytics
     */
    private List<Map<String, Object>> getEndpointAnalytics() {
        return endpointMetrics.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotalRequests(), a.getTotalRequests()))
                .map(this::endpointMetricsToMap)
                .collect(Collectors.toList());
    }

    /**
     * Recent API Activity
     */
    private List<Map<String, Object>> getRecentActivity() {
        return requestLogs.stream()
                .sorted((a, b) -> b.timestamp.compareTo(a.timestamp))
                .limit(20)
                .map(this::requestLogToMap)
                .collect(Collectors.toList());
    }

    /**
     * Performance Insights and Recommendations
     */
    private List<String> getPerformanceInsights() {
        List<String> insights = new ArrayList<>();

        long totalReqs = totalRequests.get();
        long totalErrs = totalErrors.get();

        // Error rate analysis
        if (totalReqs > 10 && totalErrs > 0) {
            double errorRate = (totalErrs * 100.0 / totalReqs);
            if (errorRate > 5.0) {
                insights.add("⚠️ HIGH ERROR RATE: " + String.format("%.1f%%", errorRate) +
                        " - Consider investigating validation failures");
            } else if (errorRate > 1.0) {
                insights.add("⚡ MODERATE ERROR RATE: " + String.format("%.1f%%", errorRate) +
                        " - Monitor for patterns");
            }
        }

        // Performance analysis
        if (totalReqs > 0) {
            long avgResponseTime = totalExecutionTime.get() / totalReqs;
            if (avgResponseTime > 1000) {
                insights.add("🐌 SLOW RESPONSE TIMES: Average " + avgResponseTime +
                        "ms - Consider database optimization");
            } else if (avgResponseTime > 500) {
                insights.add("⏱️ MODERATE RESPONSE TIMES: Average " + avgResponseTime +
                        "ms - Performance is acceptable");
            } else {
                insights.add("🚀 EXCELLENT RESPONSE TIMES: Average " + avgResponseTime +
                        "ms - API performing optimally");
            }
        }

        // Endpoint popularity insights
        if (!endpointMetrics.isEmpty()) {
            EndpointMetrics mostPopular = endpointMetrics.values().stream()
                    .max((a, b) -> Long.compare(a.getTotalRequests(), b.getTotalRequests()))
                    .orElse(null);

            if (mostPopular != null && mostPopular.getTotalRequests() > 5) {
                insights.add("📈 MOST POPULAR ENDPOINT: " + mostPopular.getEndpoint() +
                        " (" + mostPopular.getTotalRequests() + " requests)");
            }
        }

        // Validation insights
        long validationErrors = requestLogs.stream()
                .mapToLong(log -> log.endpoint.contains("/validate") && !log.success ? 1 : 0)
                .sum();

        if (validationErrors > 0) {
            insights.add("🛡️ VALIDATION WORKING: " + validationErrors +
                    " validation failures blocked invalid data");
        }

        if (insights.isEmpty()) {
            insights.add("✅ SYSTEM HEALTHY: All metrics within normal ranges");
        }

        return insights;
    }

    /**
     * Get formatted system uptime
     */
    private String getFormattedUptime() {
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(systemStartTime, now).toHours();
        long minutes = java.time.Duration.between(systemStartTime, now).toMinutes() % 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }

    /**
     * Convert endpoint metrics to map for JSON response
     */
    private Map<String, Object> endpointMetricsToMap(EndpointMetrics metrics) {
        Map<String, Object> map = new HashMap<>();
        map.put("endpoint", metrics.getEndpoint());
        map.put("totalRequests", metrics.getTotalRequests());
        map.put("successfulRequests", metrics.getSuccessfulRequests());
        map.put("failedRequests", metrics.getFailedRequests());
        map.put("successRate", metrics.getSuccessRate() + "%");
        map.put("averageResponseTime", metrics.getAverageResponseTime() + "ms");
        map.put("minResponseTime", metrics.getMinResponseTime() + "ms");
        map.put("maxResponseTime", metrics.getMaxResponseTime() + "ms");
        return map;
    }

    /**
     * Convert request log to map for JSON response
     */
    private Map<String, Object> requestLogToMap(ApiRequestLog log) {
        Map<String, Object> map = new HashMap<>();
        map.put("requestId", log.requestId);
        map.put("endpoint", log.endpoint);
        map.put("method", log.method);
        map.put("executionTime", log.executionTimeMs + "ms");
        map.put("success", log.success);
        map.put("status", log.success ? "SUCCESS" : "ERROR");
        map.put("timestamp", log.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return map;
    }

    /**
     * Endpoint-specific metrics storage
     */
    private static class EndpointMetrics {
        private final String endpoint;
        private final AtomicLong totalRequests = new AtomicLong(0);
        private final AtomicLong successfulRequests = new AtomicLong(0);
        private final AtomicLong failedRequests = new AtomicLong(0);
        private final AtomicLong totalExecutionTime = new AtomicLong(0);
        private final AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong maxResponseTime = new AtomicLong(0);

        public EndpointMetrics(String endpoint) {
            this.endpoint = endpoint;
        }

        public void recordRequest(long executionTimeMs, boolean success) {
            totalRequests.incrementAndGet();
            totalExecutionTime.addAndGet(executionTimeMs);

            if (success) {
                successfulRequests.incrementAndGet();
            } else {
                failedRequests.incrementAndGet();
            }

            // Update min/max response times
            minResponseTime.updateAndGet(current -> Math.min(current, executionTimeMs));
            maxResponseTime.updateAndGet(current -> Math.max(current, executionTimeMs));
        }

        // Getters
        public String getEndpoint() { return endpoint; }
        public long getTotalRequests() { return totalRequests.get(); }
        public long getSuccessfulRequests() { return successfulRequests.get(); }
        public long getFailedRequests() { return failedRequests.get(); }
        public double getSuccessRate() {
            long total = totalRequests.get();
            return total > 0 ? (successfulRequests.get() * 100.0 / total) : 100.0;
        }
        public long getAverageResponseTime() {
            long total = totalRequests.get();
            return total > 0 ? (totalExecutionTime.get() / total) : 0;
        }
        public long getMinResponseTime() {
            long min = minResponseTime.get();
            return min == Long.MAX_VALUE ? 0 : min;
        }
        public long getMaxResponseTime() { return maxResponseTime.get(); }
    }

    /**
     * API request log entry
     */
    private static class ApiRequestLog {
        public final String requestId;
        public final String endpoint;
        public final String method;
        public final long executionTimeMs;
        public final boolean success;
        public final LocalDateTime timestamp;

        public ApiRequestLog(String requestId, String endpoint, String method,
                             long executionTimeMs, boolean success, LocalDateTime timestamp) {
            this.requestId = requestId;
            this.endpoint = endpoint;
            this.method = method;
            this.executionTimeMs = executionTimeMs;
            this.success = success;
            this.timestamp = timestamp;
        }
    }
}