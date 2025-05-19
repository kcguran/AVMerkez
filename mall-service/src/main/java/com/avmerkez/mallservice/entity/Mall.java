package com.avmerkez.mallservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.locationtech.jts.geom.Point;

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

    @Column(columnDefinition = "geometry(Point,4326)") // SRID 4326 for WGS 84
    private Point location;

    @Column(length = 100)
    private String workingHours;

    @Column
    private String website;

    @Column(length = 20)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT") // For a list of services, potentially comma-separated or JSON
    private String services;

    @Column(columnDefinition = "TEXT") // For a list of floor plan URLs, potentially comma-separated or JSON
    private String floorPlans;

    @Column
    private Integer popularityScore;

    @OneToMany(mappedBy = "mall", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<Facility> facilities = new java.util.ArrayList<>();

    // PRD'deki diğer alanlar eklenecek (latitude, longitude, workingHours, website, phone etc.)
    // private Double latitude;
    // private Double longitude;
    // private String workingHours;
    // private String website;
    // private String phone;

} 