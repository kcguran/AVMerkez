package com.avmerkez.storeservice.repository;

import com.avmerkez.storeservice.entity.Category;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
// Disable Spring Cloud Config and Eureka for tests
// @org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters(
//    @org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters.zowelListeners() // This was incorrect
// )
class CategoryRepositoryIntegrationTest {

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
    private CategoryRepository categoryRepository;

    private Category electronics, clothing, mensClothing;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        electronics = new Category("Electronics");
        clothing = new Category("Clothing");
        categoryRepository.saveAll(List.of(electronics, clothing));

        mensClothing = new Category("Men's Clothing");
        mensClothing.setParentCategory(clothing);
        categoryRepository.save(mensClothing);
    }

    @Test
    @DisplayName("findByName should return category when name exists")
    void findByName_ExistingName_ReturnsCategory() {
        Optional<Category> found = categoryRepository.findByName("Electronics");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("findByName should return empty when name does not exist")
    void findByName_NonExistingName_ReturnsEmpty() {
        Optional<Category> found = categoryRepository.findByName("NonExistent");
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("findByParentCategoryIsNull should return root categories")
    void findByParentCategoryIsNull_ReturnsRootCategories() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        assertThat(rootCategories).hasSize(2).extracting(Category::getName).containsExactlyInAnyOrder("Electronics", "Clothing");
    }

    @Test
    @DisplayName("findByParentCategoryId should return subcategories")
    void findByParentCategoryId_ReturnsSubcategories() {
        List<Category> subcategories = categoryRepository.findByParentCategoryId(clothing.getId());
        assertThat(subcategories).hasSize(1).extracting(Category::getName).containsExactly("Men's Clothing");
    }

    @Test
    @DisplayName("Saving a category should assign an ID")
    void saveCategory_AssignsId() {
        Category newCategory = new Category("Books");
        Category savedCategory = categoryRepository.save(newCategory);
        assertThat(savedCategory.getId()).isNotNull();
    }
} 