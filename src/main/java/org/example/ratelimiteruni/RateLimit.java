package org.example.ratelimiteruni;

public interface RateLimit {
    boolean isAllowed(String Key, long capacity, double tokensPerSecond);
    boolean isBlacklisted(String key);
    void reportViolation(String key, long banThreshold);
}
