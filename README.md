
# Rate Limiter Uni — Distributed Rate Limiting (Spring Boot + Redis)

Production-ready token-bucket rate limiting backed by Redis. Works with Spring Boot 3 (Jakarta), supports per-endpoint and per-tier limits, user/IP identification, informative headers, and a fail-open option for resilience.

## Quick Start
- **Prerequisites:** Java 17+, Maven 3.6+, Redis 6+
- **Configure Redis** in your `application.properties`:
  ```properties
  spring.data.redis.host=localhost
  spring.data.redis.port=6379
  spring.data.redis.password=
  ```
- **Start Redis** (example with Docker):
  ```bash
  docker run -p 6379:6379 redis:7
  ```
- **Run the application:**
  ```bash
  mvn spring-boot:run
  ```

## Features
- Redis-backed token bucket for distributed limits
- Per-endpoint and per-tier rules (anonymous, authenticated, premium, etc.)
- User or IP-based rate limit keys
- Standard rate-limit headers and 429 responses on exceed
- Optional fail-open when Redis is unavailable
- JWT token support for automatic user identification

## Use This Rate Limiter in Your Project

1. **Add the dependency** — Copy the rate-limiter module into your project or publish it as an internal artifact.

2. **Configure Redis** — Set connection properties in `application.properties` or `application.yml`.

3. **Register the HTTP filter** — Ensure the rate-limit filter is applied early in the request chain so every request is evaluated.

4. **Define rate limit rules** — Specify:
    - Bucket capacity (max tokens)
    - Refill rate (tokens per second)
    - Path patterns (e.g., `/api/*`, `/public/*`)
    - User tiers (anonymous, authenticated, premium)

5. **Identify callers** — Extract a stable user ID (e.g., from JWT Bearer token) and fall back to client IP for unauthenticated requests.

6. **Handle responses** — Return standard headers:
    - `X-RateLimit-Limit` — Maximum requests allowed
    - `X-RateLimit-Remaining` — Remaining requests
    - `X-RateLimit-Reset` — Reset time (Unix timestamp)
    - Return HTTP 429 (Too Many Requests) when limits are exceeded

7. **Test and tune** — Load test your configuration, adjust capacities/refill rates, and monitor Redis performance.

## Notes
- Start with conservative defaults and short TTLs on rate-limit keys
- Group endpoints (e.g., `/public`, `/auth`, `/api/*`) with appropriate tier-based limits
- Enable fail-open if rate limiting is non-critical to service availability