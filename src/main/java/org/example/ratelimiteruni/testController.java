//changes with every push to test the features of rate limiter
package org.example.ratelimiteruni;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping("/login")
    public String test() {
        return "Welcome! You are inside the club. \uD83C\uDF89";
    }
}