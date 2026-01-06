package org.example.ratelimiteruni;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Base64;

@Component
public class RateLimitFilter implements Filter {

    private final RateLimit rateLimiter;

    public RateLimitFilter(RateLimit rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        RateLimitConfig config = determineRules(req);

        if (rateLimiter.isAllowed(config.key, config.capacity, config.refillRate)) {
            chain.doFilter(request, response);
        } else {
            res.setStatus(429);
            res.getWriter().write("Too many requests! You are limited to " + config.capacity + " requests.");
        }
    }
    private RateLimitConfig determineRules(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");

        String key = "ip:" + req.getRemoteAddr();
        long capacity = 10;
        double refillRate = 1.0;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String userId = getUserIdFromToken(token);

            if (userId != null) {
                key = "user:" + userId;

                // CHECK: Is it a Premium User?
                if (userId.toLowerCase().contains("premium")) {
                    capacity = 100;
                    refillRate = 10.0;
                } else {
                    capacity = 30;
                    refillRate = 3.0;
                }
            }
        }
        return new RateLimitConfig(key, capacity, refillRate);
    }

    private static class RateLimitConfig {
        String key;
        long capacity;
        double refillRate;

        public RateLimitConfig(String key, long capacity, double refillRate) {
            this.key = key;
            this.capacity = capacity;
            this.refillRate = refillRate;
        }
    }

    private String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

            return extractValueFromJson(payloadJson, "sub");

        } catch (Exception e) {
            return null;
        }
    }

    private String extractValueFromJson(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = json.indexOf(searchKey);
        if (start == -1) return null;

        start += searchKey.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;

        return json.substring(start, end);
    }
}