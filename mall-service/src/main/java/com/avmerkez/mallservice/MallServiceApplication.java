package com.avmerkez.mallservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for the Mall Service.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MallServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallServiceApplication.class, args);
    }

} 