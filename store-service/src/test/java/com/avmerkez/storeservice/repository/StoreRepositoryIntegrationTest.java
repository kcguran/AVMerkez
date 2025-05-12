package com.avmerkez.storeservice.repository;

import com.avmerkez.storeservice.entity.Brand;
import com.avmerkez.storeservice.entity.Category;
import com.avmerkez.storeservice.entity.Store;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class StoreRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration,classpath:db/testdata"); // Include test data if any
        registry.add("spring.cloud.config.enabled", () -> false);
        registry.add("spring.cloud.bootstrap.enabled", () -> false);
        registry.add("spring.config.import", () -> "");
        registry.add("eureka.client.enabled", () -> false);
    }

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CategoryRepository categoryRepository; // To setup test data
    @Autowired
    private BrandRepository brandRepository;       // To setup test data

    private Category techCategory;
    private Category fashionCategory;
    private Brand awesomeBrand;
    private Brand anotherBrand;
    private Store store1, store2, store3;
    private final Long MALL_ID_1 = 1L;
    private final Long MALL_ID_2 = 2L;

    @BeforeEach
    void setUp() {
        storeRepository.deleteAll();
        categoryRepository.deleteAll();
        brandRepository.deleteAll();

        techCategory = categoryRepository.save(new Category("Tech"));
        fashionCategory = categoryRepository.save(new Category("Fashion"));

        awesomeBrand = brandRepository.save(new Brand("AwesomeBrand"));
        anotherBrand = brandRepository.save(new Brand("AnotherBrand"));

        store1 = new Store("Tech Store A", MALL_ID_1, techCategory, awesomeBrand);
        store1.setFloor("1st Floor");
        store2 = new Store("Fashion Store B", MALL_ID_1, fashionCategory, anotherBrand);
        store2.setFloor("Ground Floor");
        store3 = new Store("Tech Store C", MALL_ID_2, techCategory, awesomeBrand);
        store3.setFloor("2nd Floor");

        storeRepository.saveAll(List.of(store1, store2, store3));
    }

    @Test
    @DisplayName("findByMallId should return stores for the given mall ID")
    void findByMallId_ExistingMallId_ReturnsStores() {
        List<Store> stores = storeRepository.findByMallId(MALL_ID_1);
        assertThat(stores).hasSize(2);
        assertThat(stores).extracting(Store::getName).containsExactlyInAnyOrder("Tech Store A", "Fashion Store B");
    }

    @Test
    @DisplayName("findByMallId should return empty list for non-existing mall ID")
    void findByMallId_NonExistingMallId_ReturnsEmptyList() {
        List<Store> stores = storeRepository.findByMallId(999L);
        assertThat(stores).isEmpty();
    }

    @Test
    @DisplayName("findByCategoryId should return stores for the given category ID")
    void findByCategoryId_ExistingCategoryId_ReturnsStores() {
        List<Store> stores = storeRepository.findByCategoryId(techCategory.getId());
        assertThat(stores).hasSize(2);
        assertThat(stores).extracting(Store::getName).containsExactlyInAnyOrder("Tech Store A", "Tech Store C");
    }

    @Test
    @DisplayName("findByBrandId should return stores for the given brand ID")
    void findByBrandId_ExistingBrandId_ReturnsStores() {
        List<Store> stores = storeRepository.findByBrandId(awesomeBrand.getId());
        assertThat(stores).hasSize(2);
        assertThat(stores).extracting(Store::getName).containsExactlyInAnyOrder("Tech Store A", "Tech Store C");
    }

    @Test
    @DisplayName("findByMallIdAndCategoryId should return stores for the given mall and category ID")
    void findByMallIdAndCategoryId_ExistingIds_ReturnsStores() {
        List<Store> stores = storeRepository.findByMallIdAndCategoryId(MALL_ID_1, techCategory.getId());
        assertThat(stores).hasSize(1);
        assertThat(stores.get(0).getName()).isEqualTo("Tech Store A");
    }

    @Test
    @DisplayName("findByMallIdAndBrandId should return stores for the given mall and brand ID")
    void findByMallIdAndBrandId_ExistingIds_ReturnsStores() {
        List<Store> stores = storeRepository.findByMallIdAndBrandId(MALL_ID_1, awesomeBrand.getId());
        assertThat(stores).hasSize(1);
        assertThat(stores.get(0).getName()).isEqualTo("Tech Store A");
    }

    @Test
    @DisplayName("Saving a store should assign an ID")
    void saveStore_AssignsId() {
        Store newStore = new Store("New Unique Store", 3L, fashionCategory, anotherBrand);
        Store savedStore = storeRepository.save(newStore);
        assertThat(savedStore.getId()).isNotNull();
    }
} 