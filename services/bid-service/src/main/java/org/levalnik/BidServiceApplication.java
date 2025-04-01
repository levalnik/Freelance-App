package org.levalnik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BidServiceApplication {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "local");
        SpringApplication.run(BidServiceApplication.class, args);
    }
}
