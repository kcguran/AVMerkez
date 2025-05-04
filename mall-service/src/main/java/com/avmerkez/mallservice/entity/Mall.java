package com.avmerkez.mallservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "malls")
@Data // Lombok: Getter, Setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String district;

    // PRD'deki diğer alanlar eklenecek (latitude, longitude, workingHours, website, phone etc.)
    // private Double latitude;
    // private Double longitude;
    // private String workingHours;
    // private String website;
    // private String phone;

} 