package com.avmerkez.mallservice.repository;

import com.avmerkez.mallservice.entity.Mall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
                    "spring.cloud.config.fail-fast=false", // Explicitly disable fail-fast for config client in tests
                    "spring.cloud.bootstrap.enabled=false", // Disable bootstrap context for tests
                    "spring.config.import=", // Override/disable config server import for tests
                    // "spring.cloud.config.enabled=false"  // application.properties dosyasından yüklenecek
                })
@ActiveProfiles("test")
@Testcontainers
class MallRepositoryIntegrationTest {

    // PostgreSQL container'ını tanımla
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // Dinamik olarak datasource özelliklerini ayarla
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Flyway'i bu test için devre dışı bırakabiliriz veya çalıştırabiliriz.
        // Şimdilik Hibernate'in ddl-auto'sunu (testte create-drop olur genelde) kullanabiliriz.
        // registry.add("spring.flyway.enabled", () -> "false"); // Opsiyonel
        // registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop"); // Test için şemayı oluştur/bırak
        // Flyway ayarlarını ekle
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true"); // Flyway'i etkinleştir
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate"); // Hibernate'in şemayı değiştirmesini engelle, Flyway yapsın
    }

    @Autowired
    private MallRepository mallRepository;

    private Mall mall1;
    private Mall mall2;

    @BeforeEach
    void setUp() {
        // Her testten önce DB'yi temizle ve örnek veri ekle
        mallRepository.deleteAll();

        mall1 = Mall.builder().name("Mall A").city("Istanbul").district("Kadikoy").address("Address A").build();
        mall2 = Mall.builder().name("Mall B").city("Ankara").district("Cankaya").address("Address B").build();

        mallRepository.saveAll(List.of(mall1, mall2));
    }

    @Test
    @DisplayName("Save Mall Test")
    void givenMallObject_whenSave_thenReturnSavedMall() {
        // Given
        Mall newMall = Mall.builder().name("Mall C").city("Izmir").district("Konak").address("Address C").build();

        // When
        Mall savedMall = mallRepository.save(newMall);

        // Then
        assertThat(savedMall).isNotNull();
        assertThat(savedMall.getId()).isNotNull();
        assertThat(savedMall.getName()).isEqualTo("Mall C");
    }

    @Test
    @DisplayName("Find All Malls Test")
    void givenMallsExist_whenFindAll_thenReturnListOfMalls() {
        // When
        List<Mall> malls = mallRepository.findAll();

        // Then
        assertThat(malls).isNotNull();
        assertThat(malls.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Find Mall by ID Test - Exists")
    void givenMallId_whenFindById_thenReturnMall() {
        // When
        Optional<Mall> foundMall = mallRepository.findById(mall1.getId());

        // Then
        assertThat(foundMall).isPresent();
        assertThat(foundMall.get().getName()).isEqualTo(mall1.getName());
    }

    @Test
    @DisplayName("Find Mall by ID Test - Not Exists")
    void givenNonExistentMallId_whenFindById_thenReturnEmptyOptional() {
        // When
        Optional<Mall> foundMall = mallRepository.findById(999L);

        // Then
        assertThat(foundMall).isNotPresent();
    }

    @Test
    @DisplayName("Update Mall Test")
    void givenMallObject_whenUpdate_thenReturnUpdatedMall() {
        // Given
        Optional<Mall> mallToUpdateOpt = mallRepository.findById(mall1.getId());
        assertThat(mallToUpdateOpt).isPresent();
        Mall mallToUpdate = mallToUpdateOpt.get();
        mallToUpdate.setName("Updated Mall A");
        mallToUpdate.setAddress("Updated Address A");

        // When
        Mall updatedMall = mallRepository.save(mallToUpdate);

        // Then
        assertThat(updatedMall).isNotNull();
        assertThat(updatedMall.getName()).isEqualTo("Updated Mall A");
        assertThat(updatedMall.getAddress()).isEqualTo("Updated Address A");
    }

    @Test
    @DisplayName("Delete Mall Test")
    void givenMallId_whenDelete_thenMallShouldBeDeleted() {
        // Given
        Long mallIdToDelete = mall1.getId();
        assertThat(mallRepository.findById(mallIdToDelete)).isPresent();

        // When
        mallRepository.deleteById(mallIdToDelete);

        // Then
        Optional<Mall> deletedMall = mallRepository.findById(mallIdToDelete);
        assertThat(deletedMall).isNotPresent();
        assertThat(mallRepository.count()).isEqualTo(1);
    }
} 