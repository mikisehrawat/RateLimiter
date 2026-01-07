# Rate Limiter Uni - Distributed Rate Limiting with Spring Boot & Redis

A production-ready distributed rate limiting solution using Spring Boot, Jakarta EE, and Redis. This project implements a token bucket algorithm with configurable rate limits for different API endpoints and user tiers.

## 📋 Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)


## ✨ Features

- **Token Bucket Algorithm**: Efficient rate limiting using Redis-backed token buckets
- **Multi-Tier Support**: Different rate limits for anonymous users, authenticated users, and premium members
- **Endpoint-Based Rules**: Configurable rate limits per endpoint
- **Distributed Rate Limiting**: Powered by Redis for horizontal scalability
- **User & IP-Based Tracking**: Rate limit by IP address or authenticated user ID
- **Informative HTTP Headers**: Clear response headers indicating rate limit status
- **Fail-Open Strategy**: Graceful degradation when Redis is unavailable
- **JWT Token Support**: Automatic user identification from Bearer tokens
- **Spring Boot Integration**: Seamless integration as a servlet filter

## 🏗️ Architecture

### Components

1. **RateLimitFilter** (Filter):
    - Intercepts all incoming HTTP requests
    - Determines applicable rate limit rules based on endpoint and user tier
    - Enforces rate limits and returns 429 (Too Many Requests) when exceeded

2. **RateLimit** (Interface):
    - Defines the contract for rate limiting implementations
    - Exposes the `isAllowed()` method for permission checks

3. **SimpleRedisRateLimiter** (Implementation):
    - Implements the token bucket algorithm using Redis
    - Manages token refill and consumption
    - Handles distributed state across multiple instances

4. **RedisConfig** (Configuration):
    - Sets up Redis connection and Spring Data Redis infrastructure
    - Configures the RedisTemplate for interaction with Redis

## 📦 Prerequisites

- Java 17+
- Maven 3.6+
- Redis 6.0+
- Spring Boot 3.x
- Jakarta EE (Jakarta Servlet, Jakarta Inject)

## 🚀 Installation

### 1. Clone the Repository

### 2. Build the Project

### 3. Start Redis
Using Docker Compose:


Or use an existing Redis instance and update `application.properties` accordingly.

### 4. Run the Application
```bash
mvn spring-boot:run
```