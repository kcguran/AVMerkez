package com.avmerkez.mallservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MallCreatedEvent {
    private Long id;
    private String name;
    private String city;
    private String district;
    private String description;
    private double latitude;
    private double longitude;
} 