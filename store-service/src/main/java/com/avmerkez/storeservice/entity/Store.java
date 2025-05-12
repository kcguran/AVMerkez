package com.avmerkez.storeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Long mallId; // This will link to AlisverisMerkezi in mall-service, handled via API calls

    @Size(max = 50)
    private String floor;

    @Size(max = 50)
    private String storeNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(columnDefinition = "TEXT")
    private String contactInformation; // Could be JSON or semi-structured text

    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 255)
    private String logoUrl;

    public Store(String name, Long mallId, Category category, Brand brand) {
        this.name = name;
        this.mallId = mallId;
        this.category = category;
        this.brand = brand;
    }
} 