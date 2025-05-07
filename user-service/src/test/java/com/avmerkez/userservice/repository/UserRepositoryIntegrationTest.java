package com.avmerkez.userservice.repository;

import com.avmerkez.userservice.entity.Role;
import com.avmerkez.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@EnableAutoConfiguration
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.bootstrap.enabled=false",
    "spring.config.import=",
    "eureka.client.enabled=false"
})
class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.cloud.config.enabled", () -> "false");
        registry.add("spring.cloud.bootstrap.enabled", () -> "false");
        registry.add("spring.config.import", () -> "");
    }

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User("testuser", "test@example.com", "encodedPassword", "Test", "User");
        testUser.setRoles(Set.of(Role.USER));
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("findByUsername should return user when username exists")
    void findByUsername_ExistingUser_ReturnsUser() {
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("findByUsername should return empty when username does not exist")
    void findByUsername_NonExistingUser_ReturnsEmpty() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("findByEmail should return user when email exists")
    void findByEmail_ExistingEmail_ReturnsUser() {
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("findByEmail should return empty when email does not exist")
    void findByEmail_NonExistingEmail_ReturnsEmpty() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("existsByUsername should return true when username exists")
    void existsByUsername_ExistingUser_ReturnsTrue() {
        Boolean exists = userRepository.existsByUsername("testuser");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUsername should return false when username does not exist")
    void existsByUsername_NonExistingUser_ReturnsFalse() {
        Boolean exists = userRepository.existsByUsername("nonexistent");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByEmail should return true when email exists")
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        Boolean exists = userRepository.existsByEmail("test@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail should return false when email does not exist")
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        Boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Saving user should assign an ID and createdAt timestamp")
    void saveUser_AssignsIdAndTimestamp() {
        User newUser = new User("newuser", "new@example.com", "password", "New", "User");
        newUser.setRoles(Set.of(Role.USER));
        User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(userRepository.count()).isEqualTo(2);
    }
} 