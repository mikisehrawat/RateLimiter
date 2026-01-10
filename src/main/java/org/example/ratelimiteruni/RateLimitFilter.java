package org.example.ratelimiteruni;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Base64;

@Component
public class RateLimitFilter implements Filter {

    private final RateLimit rateLimiter;

    @Value("${ratelimit.login.capacity:1}")
    long logincapacity;
    @Value("${ratelimit.login.refillRate:1.0}")
    double loginrefillRate;
    @Value("${ratelimit.general.capacity:1}")
    long generalcapacity;
    @Value("${ratelimit.general.refillRate:1.0}")
    double generalrefillRate;
    @Value("${ratelimit.data.capacity:1}")
    long datacapacity;
    @Value("${ratelimit.data.refillRate:1.0}")
    double datarefillRate;
    @Value("${ratelimit.premium.multiplier:1}")
    long premiumMultiplier;
    @Value("${ratelimit.user.multiplier:1}")
    long userMultiplier;

    public RateLimitFilter(RateLimit rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        RateLimitConfig configApi = accApi(req);
        RateLimitConfig config = determineRules(req,configApi.capacity, configApi.refillRate,configApi.key);

        if (rateLimiter.isBlacklisted(config.key)) {
            res.setStatus(403); // 403 = Forbidden (Stricter than 429)
            res.getWriter().write("You are temporarily banned for spamming. Try again in 1 hour.");
            return;
        }

        if (rateLimiter.isAllowed(config.key, config.capacity, config.refillRate)) {
            chain.doFilter(request, response);
        } else {
            rateLimiter.reportViolation(config.key, config.capacity);
            res.setStatus(429);
            res.getWriter().write("Too many requests! You are limited to " + config.capacity + " requests.");
        }
    }
    private RateLimitConfig accApi(HttpServletRequest req){
        String uri = req.getRequestURI(); // e.g., "/login", "/data", "/home"
        String finalKey;
        long capacity;
        double refillRate;
        if (uri.equals("/login") || uri.equals("/signup")) {
            capacity = logincapacity;
            refillRate = loginrefillRate;
            finalKey = "/login";
        }
        else if (uri.equals("/data")) {
            capacity = datacapacity;
            refillRate = datarefillRate;
            finalKey = "/data";
        }
        else {
            capacity = generalcapacity;
            refillRate = generalrefillRate;
            finalKey = "/general";
        }

        return new RateLimitConfig(finalKey, capacity, refillRate);
    }

    private RateLimitConfig determineRules(HttpServletRequest req,long c, double r,String ApiType) {
        String authHeader = req.getHeader("Authorization");

        String key = "ip:" + getClientIp(req);
        long capacity = c;
        double refillRate = r;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String userId = getUserIdFromToken(token);

            if (userId != null) {
                key = "user:" + userId;
                if (userId.toLowerCase().contains("premium")) {
                    capacity = c*premiumMultiplier;
                    refillRate = r*premiumMultiplier;
                } else {
                    capacity = c*userMultiplier;
                    refillRate = r*userMultiplier;
                }
            }
        }
        return new RateLimitConfig(key+ApiType, capacity, refillRate);
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
    private String getClientIp(HttpServletRequest req) {
        String ipAddress = req.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            return req.getRemoteAddr();
        }
        if (ipAddress.contains(",")) {
            return ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }
}