package com.avmerkez.reviewservice.repository;

import com.avmerkez.reviewservice.entity.ApprovalStatus;
import com.avmerkez.reviewservice.entity.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Minimal Spring configuration for repository tests that completely avoids Spring Cloud
 * 
 * Bu testler Spring Cloud Config Server'a bağlanma sorunu nedeniyle devre dışı bırakılmıştır.
 * İleride bu sorunu çözmek için:
 * 1. Testler için ayrı bir Spring Context hazırlanmalı
 * 2. Spring Cloud Config devre dışı bırakılmalı
 * 3. Test veritabanı bağlantısı manuel olarak ayarlanmalı
 */
@Disabled("Spring Cloud Config Server bağlantı sorunları nedeniyle devre dışı bırakıldı")
@SpringJUnitConfig  // Use Spring's JUnit support
@DataJpaTest        // Configure JPA repositories
@Testcontainers     // Enable test containers support
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.bootstrap.enabled=false", 
    "spring.cloud.config.enabled=false",
    "spring.config.import=optional:",
    "eureka.client.enabled=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {

    /**
     * Basic configuration to provide only what we need for JPA tests
     */
    @SpringBootApplication(
        scanBasePackages = {
            "com.avmerkez.reviewservice.entity",
            "com.avmerkez.reviewservice.repository"
        },
        exclude = {
            // Disable all Spring Cloud related auto-configuration
            org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration.class,
            org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration.class,
            org.springframework.cloud.commons.util.UtilAutoConfiguration.class,
            org.springframework.cloud.client.CommonsClientAutoConfiguration.class,
            org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        }
    )
    static class TestConfig {
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ReviewRepository reviewRepository;
    
    private Long mallId1 = 1L;
    private Long mallId2 = 2L;
    private Long storeId1 = 10L;
    private Long userId1 = 100L;
    private Long userId2 = 101L;
    
    private Review r1, r2, r3, r4, r5; // Hold instances for assertion

    @BeforeEach
    void setUp() {
        // Configure database connection for tests
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        System.setProperty("spring.jpa.hibernate.ddl-auto", "create-drop");
        
        reviewRepository.deleteAll(); // Clean slate before each test
        
        // Create test data
        r1 = new Review(null, userId1, mallId1, null, 5, "Mall 1 Great", Instant.now().minusSeconds(100), null, ApprovalStatus.APPROVED, Instant.now().minusSeconds(50), 99L, null);
        r2 = new Review(null, userId2, mallId1, null, 4, "Mall 1 Good", Instant.now().minusSeconds(200), null, ApprovalStatus.APPROVED, Instant.now().minusSeconds(150), 99L, null);
        r3 = new Review(null, userId1, mallId1, null, 3, "Mall 1 Pending", Instant.now().minusSeconds(50), null, ApprovalStatus.PENDING, null, null, null);
        r4 = new Review(null, userId1, null, storeId1, 5, "Store 1 Awesome", Instant.now().minusSeconds(300), null, ApprovalStatus.APPROVED, Instant.now().minusSeconds(250), 99L, null);
        r5 = new Review(null, userId2, mallId2, null, 2, "Mall 2 Bad", Instant.now().minusSeconds(10), null, ApprovalStatus.REJECTED, Instant.now().minusSeconds(5), 99L, "Bad language");
        
        reviewRepository.saveAll(List.of(r1, r2, r3, r4, r5));
    }
    
    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
    }
    
    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }
    
    @Test
    void findByMallIdAndApprovalStatus_ShouldReturnApprovedReviews() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> resultPage = reviewRepository.findByMallIdAndApprovalStatus(mallId1, ApprovalStatus.APPROVED, pageable);
        
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        List<Review> reviews = resultPage.getContent();
        assertThat(reviews).extracting(Review::getComment).containsExactlyInAnyOrder("Mall 1 Great", "Mall 1 Good");
        assertThat(reviews).allMatch(review -> review.getApprovalStatus() == ApprovalStatus.APPROVED);
    }
    
    @Test
    void findByStoreIdAndApprovalStatus_ShouldReturnApprovedReviews() {
         Pageable pageable = PageRequest.of(0, 5);
         Page<Review> resultPage = reviewRepository.findByStoreIdAndApprovalStatus(storeId1, ApprovalStatus.APPROVED, pageable);
         
         assertThat(resultPage).isNotNull();
         assertThat(resultPage.getTotalElements()).isEqualTo(1);
         Review review = resultPage.getContent().get(0);
         assertThat(review.getComment()).isEqualTo("Store 1 Awesome");
         assertThat(review.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
    }
    
    @Test
    void findTop5ByMallIdAndApprovalStatusOrderByCreatedAtDesc_ShouldReturnLatestApproved() {
        List<Review> result = reviewRepository.findTop5ByMallIdAndApprovalStatusOrderByCreatedAtDesc(mallId1, ApprovalStatus.APPROVED);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2); // Based on current setup
        assertThat(result.get(0).getComment()).isEqualTo("Mall 1 Great"); // Should be the latest created (r1)
        assertThat(result.get(1).getComment()).isEqualTo("Mall 1 Good");  // r2
        assertThat(result).allMatch(review -> review.getApprovalStatus() == ApprovalStatus.APPROVED);
    }
    
    @Test
    void findTop5ByStoreIdAndApprovalStatusOrderByCreatedAtDesc_ShouldReturnLatestApproved() {
        List<Review> result = reviewRepository.findTop5ByStoreIdAndApprovalStatusOrderByCreatedAtDesc(storeId1, ApprovalStatus.APPROVED);
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getComment()).isEqualTo("Store 1 Awesome");
        assertThat(result.get(0).getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
    }
    
    @Test
    void findByApprovalStatus_ShouldReturnPendingReviews() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> resultPage = reviewRepository.findByApprovalStatus(ApprovalStatus.PENDING, pageable);
        
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        Review review = resultPage.getContent().get(0);
        assertThat(review.getComment()).isEqualTo("Mall 1 Pending");
        assertThat(review.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);
    }
    
    @Test
    void findByUserIdAndApprovalStatus_ShouldReturnApprovedReviews() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> resultPage = reviewRepository.findByUserIdAndApprovalStatus(userId1, ApprovalStatus.APPROVED, pageable);
        
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(2); // r1 and r4
        List<Review> reviews = resultPage.getContent();
        assertThat(reviews).extracting(Review::getComment).containsExactlyInAnyOrder("Mall 1 Great", "Store 1 Awesome");
        assertThat(reviews).allMatch(review -> review.getApprovalStatus() == ApprovalStatus.APPROVED);
    }
    
    @Test
    void findByUserId_ShouldReturnAllReviewsForUser() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> resultPage = reviewRepository.findByUserId(userId1, pageable);
        
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(3); // r1, r3, r4
        List<Review> reviews = resultPage.getContent();
        assertThat(reviews).extracting(Review::getComment).containsExactlyInAnyOrder("Mall 1 Great", "Mall 1 Pending", "Store 1 Awesome");
        assertThat(reviews).allMatch(r -> r.getUserId().equals(userId1));
    }
} 