package com.avmerkez.storeservice.repository;

import com.avmerkez.storeservice.entity.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BrandRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.cloud.config.enabled", () -> false);
        registry.add("spring.cloud.bootstrap.enabled", () -> false);
        registry.add("spring.config.import", () -> "");
        registry.add("eureka.client.enabled", () -> false);
    }

    @Autowired
    private BrandRepository brandRepository;

    private Brand brand1;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
        brand1 = new Brand("Awesome Brand");
        brand1.setLogoUrl("logo.png");
        brand1.setWebsite("example.com");
        brandRepository.save(brand1);
    }

    @Test
    @DisplayName("findByName should return brand when name exists")
    void findByName_ExistingName_ReturnsBrand() {
        Optional<Brand> found = brandRepository.findByName("Awesome Brand");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Awesome Brand");
    }

    @Test
    @DisplayName("findByName should return empty when name does not exist")
    void findByName_NonExistingName_ReturnsEmpty() {
        Optional<Brand> found = brandRepository.findByName("NonExistent Brand");
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Saving a brand should assign an ID")
    void saveBrand_AssignsId() {
        Brand newBrand = new Brand("New Brand");
        Brand savedBrand = brandRepository.save(newBrand);
        assertThat(savedBrand.getId()).isNotNull();
    }
} 