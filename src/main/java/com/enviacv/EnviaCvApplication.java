package com.enviacv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnviaCvApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnviaCvApplication.class, args);
    }
}