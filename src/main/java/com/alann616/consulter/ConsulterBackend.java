package com.alann616.consulter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ConsulterBackend {
    public static void main(String[] args) {
        SpringApplication.run(ConsulterBackend.class, args);
    }
}
