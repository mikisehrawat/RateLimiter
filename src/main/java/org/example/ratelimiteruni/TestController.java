package org.example.ratelimiteruni;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Welcome! You are inside the club. \uD83C\uDF89";
    }
}