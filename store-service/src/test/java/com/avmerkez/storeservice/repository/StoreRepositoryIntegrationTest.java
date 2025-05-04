package com.avmerkez.storeservice.repository;

import com.avmerkez.storeservice.entity.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers // Enable Testcontainers support
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use Testcontainers database
class StoreRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_store_db") // Use a specific DB name for store tests
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        // Ensure Flyway uses the test container datasource
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
        // Use store_db schema for Flyway if needed (depends on Flyway config)
        // registry.add("spring.flyway.schemas", () -> "store_db");
    }

    @Autowired
    private StoreRepository storeRepository;

    private Store store1Mall1, store2Mall1, store1Mall2;

    @BeforeEach
    void setUp() {
        storeRepository.deleteAll(); // Clean up before each test

        store1Mall1 = Store.builder()
                .mallId(1L)
                .name("Store A")
                .category("Electronics")
                .floor("1st Floor")
                .storeNumber("A-101")
                .build();
        store2Mall1 = Store.builder()
                .mallId(1L)
                .name("Store B")
                .category("Clothing")
                .floor("Ground Floor")
                .storeNumber("G-05")
                .build();
        store1Mall2 = Store.builder()
                .mallId(2L)
                .name("Store C")
                .category("Electronics")
                .floor("2nd Floor")
                .storeNumber("B-210")
                .build();

        storeRepository.saveAll(List.of(store1Mall1, store2Mall1, store1Mall2));
    }

    @Test
    @DisplayName("Find stores by existing Mall ID")
    void findByMallId_ShouldReturnStores_WhenMallIdExists() {
        // When
        List<Store> foundStores = storeRepository.findByMallId(1L);

        // Then
        assertThat(foundStores).hasSize(2);
        assertThat(foundStores).extracting(Store::getName).containsExactlyInAnyOrder("Store A", "Store B");
        assertThat(foundStores).extracting(Store::getMallId).containsOnly(1L);
    }

    @Test
    @DisplayName("Find stores by non-existing Mall ID")
    void findByMallId_ShouldReturnEmptyList_WhenMallIdDoesNotExist() {
        // When
        List<Store> foundStores = storeRepository.findByMallId(99L);

        // Then
        assertThat(foundStores).isEmpty();
    }

     @Test
    @DisplayName("Save a new store")
    void save_ShouldPersistStore() {
        // Given
        Store newStore = Store.builder()
                .mallId(3L)
                .name("New Store")
                .category("Books")
                .floor("Basement")
                .storeNumber("BS-01")
                .build();

        // When
        Store savedStore = storeRepository.save(newStore);

        // Then
        assertThat(savedStore).isNotNull();
        assertThat(savedStore.getId()).isNotNull();
        assertThat(savedStore.getName()).isEqualTo("New Store");
        assertThat(storeRepository.findById(savedStore.getId())).isPresent();
    }

    // Add more tests for other repository methods if needed (findById, findAll, deleteById etc.)
} 