package com.gmail.bishoybasily.hellogcp;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

@RestController
@SpringBootApplication
public class HelloGcpApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(HelloGcpApplication.class, args);
    }

    @GetMapping
    public String hello() {
        System.out.println("hit");
        return "hi";
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("--- Local -----------------------------------------------");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        String hostName = InetAddress.getLocalHost().getHostName();
        System.out.println(hostAddress + ", " + hostName);

        System.out.println("--- Remote -----------------------------------------------");
        String hostAddress1 = InetAddress.getLoopbackAddress().getHostAddress();
        String hostName1 = InetAddress.getLoopbackAddress().getHostName();
        System.out.println(hostAddress1 + ", " + hostName1);

    }
}
