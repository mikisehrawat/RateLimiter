package org.example.ratelimiteruni;

import redis.clients.jedis.JedisPool;

public class Main {
    public static void main(String[] args) {
        JedisPool pool = new JedisPool("localhost", 6379);
        RateLimit limiter = new SimpleRedisRateLimiter(pool, 5, 1.0);

        String ipAddress = "192.168.0.1";

        System.out.println("--- Starting Requests ---");

        for (int i = 1; i <= 10; i++) {
            boolean allowed = limiter.isAllowed(ipAddress);
            System.out.println("Request " + i + ": " + (allowed ? "Allowed ✅" : "Blocked ❌"));
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        }

        pool.close();
    }
}