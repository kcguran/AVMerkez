package com.avmerkez.campaigneventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CampaignEventServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampaignEventServiceApplication.class, args);
    }
} 