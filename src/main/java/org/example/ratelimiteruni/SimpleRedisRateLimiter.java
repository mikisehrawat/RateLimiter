package org.example.ratelimiteruni;

import com.jetbrains.exported.JBRApi;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimpleRedisRateLimiter implements RateLimit {

    private final JedisPool jedisPool;

    public SimpleRedisRateLimiter(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public boolean isAllowed(String key, long capacity, double tokensPerSecond) {
        try (Jedis jedis = jedisPool.getResource()) {
            String redisKey = "rate_limit:" + key;
            long now = Instant.now().toEpochMilli();
            List<String> data = jedis.hmget(redisKey, "tokens", "last_refill_time");
            String tokensStr = data.get(0);
            String lastRefillStr = data.get(1);

            double currentTokens;
            long lastRefillTime;
            if (tokensStr == null) {
                currentTokens = capacity;
                lastRefillTime = now;
            } else {
                currentTokens = Double.parseDouble(tokensStr);
                lastRefillTime = Long.parseLong(lastRefillStr);
            }
            long timeElapsed = now - lastRefillTime;
            double tokensToAdd = (timeElapsed / 1000.0) * tokensPerSecond;
            currentTokens = Math.min(capacity, currentTokens + tokensToAdd);
            lastRefillTime = now;
            boolean allowed = false;
            if (currentTokens >= 1) {
                currentTokens -= 1;
                allowed = true;
            }
            Map<String, String> hash = new HashMap<>();
            hash.put("tokens", String.valueOf(currentTokens));
            hash.put("last_refill_time", String.valueOf(lastRefillTime));
            jedis.hmset(redisKey, hash);
            jedis.expire(redisKey, 3600);

            return allowed;
        }
    }
}