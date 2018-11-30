package com.gmail.bishoybasily.hellogcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@SpringBootApplication
public class HelloGcpApplication {

    private Logger logger = Logger.getLogger(getClass().getName());

    public static void main(String[] args) {
        SpringApplication.run(HelloGcpApplication.class, args);
    }

    private String template = "Hello, %s";

    @GetMapping
    public String hello(@RequestParam(required = false, value = "name") String name) {
        String respones = String.format(template, name == null ? "user" : name);

        logger.info("Logging INFO with Logback");
        logger.severe("Logging ERROR with Logback");

        return respones;
    }

}
