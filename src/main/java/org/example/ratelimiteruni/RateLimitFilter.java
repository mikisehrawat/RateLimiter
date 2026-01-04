package org.example.ratelimiteruni;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class RateLimitFilter implements Filter {

    private final RateLimit rateLimiter;

    //@Primary for deciding priority of rate limiter
    //we will use @Qualifire in the future to determine which rate limiter to use, according to a request type
    public RateLimitFilter(RateLimit rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String clientIp = req.getRemoteAddr();

        if (rateLimiter.isAllowed(clientIp)) {
            chain.doFilter(request, response);
        } else {
            res.setStatus(429);
            res.getWriter().write("Too many requests! Please try again later.");
        }
    }
}