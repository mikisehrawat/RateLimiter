package org.example.ratelimiteruni;

public interface RateLimit {
    boolean isAllowed(String Key, long capacity, double tokensPerSecond);
}
