package com.gmail.bishoybasily.hellogcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class HelloGcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloGcpApplication.class, args);
    }

    @GetMapping
    public String hello() {
        return "hi";
    }

}
