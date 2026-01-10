package org.example.ratelimiteruni;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RateLimiterUniApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimiterUniApplication.class, args);
    }

}
//1. Fix the "Race Condition"
//Redis Lua Scripts Redis is single-threaded
//2. Trusting the Wrong IP (Proxy Spoofing)

//SlidingWindowRateLimiter
//Redis: Sorted Set (ZSET).
//Clean
//count
//log new call