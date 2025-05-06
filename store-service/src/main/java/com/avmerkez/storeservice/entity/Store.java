package com.avmerkez.storeservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Long mallId; // Hangi AVM'ye ait olduğu

    @Column(length = 50)
    private String floor; // Kat bilgisi (örn: "Zemin Kat", "1. Kat")

    @Column(length = 20)
    private String storeNumber; // Mağaza numarası (örn: "B1-102")

    @Column(name = "category_id")
    private Long categoryId;

    private String contactInfo; // İletişim bilgisi (telefon vb.)
    private String description; // Açıklama
    private String logoUrl; // Logo URL'si

} 