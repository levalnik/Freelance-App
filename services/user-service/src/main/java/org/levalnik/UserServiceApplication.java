package org.levalnik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "local");
        SpringApplication.run(UserServiceApplication.class, args);
    }
}