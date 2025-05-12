package com.avmerkez.mallservice.repository;

import com.avmerkez.mallservice.entity.Mall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
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

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // PostgreSQL container'ını PostGIS destekli olarak tanımla
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgis/postgis:15-3.4");

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

        // Örnek konumlar (Ankara Kızılay Meydanı civarı)
        Point locationA = geometryFactory.createPoint(new Coordinate(32.854110, 39.920770)); // Kızılay
        Point locationB = geometryFactory.createPoint(new Coordinate(32.8560, 39.9220));   // Biraz daha doğusu
        Point locationC = geometryFactory.createPoint(new Coordinate(30.7139, 36.8969));   // Antalya (uzak bir nokta)


        mall1 = Mall.builder().name("Mall A Kızılay").city("Ankara").district("Çankaya").address("Address A").location(locationA).build();
        mall2 = Mall.builder().name("Mall B Sıhhiye").city("Ankara").district("Çankaya").address("Address B").location(locationB).build();
        Mall mall3 = Mall.builder().name("Mall C Antalya").city("Antalya").district("Muratpaşa").address("Address C").location(locationC).build();

        mallRepository.saveAll(List.of(mall1, mall2, mall3));
    }

    @Test
    @DisplayName("Save Mall Test")
    void givenMallObject_whenSave_thenReturnSavedMall() {
        // Given
        Point newLocation = geometryFactory.createPoint(new Coordinate(32.7500, 39.9334)); // Anıtkabir civarı
        Mall newMall = Mall.builder().name("Mall Anıtkabir").city("Ankara").district("Çankaya").address("Address Anıtkabir").location(newLocation).build();

        // When
        Mall savedMall = mallRepository.save(newMall);

        // Then
        assertThat(savedMall).isNotNull();
        assertThat(savedMall.getId()).isNotNull();
        assertThat(savedMall.getName()).isEqualTo("Mall Anıtkabir");
    }

    @Test
    @DisplayName("Find All Malls Test")
    void givenMallsExist_whenFindAll_thenReturnListOfMalls() {
        // When
        List<Mall> malls = mallRepository.findAll();

        // Then
        assertThat(malls).isNotNull();
        assertThat(malls.size()).isEqualTo(3); // 3 AVM eklendi
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
        mallToUpdate.setLocation(geometryFactory.createPoint(new Coordinate(32.8550, 39.9210))); // Konumu da güncelle

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
        assertThat(mallRepository.count()).isEqualTo(2); // Bir AVM silindi
    }

    @Test
    @DisplayName("Find Malls Within Distance - Should Return Nearby Malls")
    void findMallsWithinDistance_thenReturnNearbyMalls() {
        // Kızılay (mall1) merkez alınarak 500 metre yarıçap içinde arama
        // mall1 (Kızılay) ve mall2 (Sıhhiye, Kızılay'a ~200-300m) bulunmalı, mall3 (Antalya) bulunmamalı.
        // Koordinatlar: mall1 (32.854110, 39.920770), mall2 (32.8560, 39.9220)
        // ST_Distance(mall1.location::geography, mall2.location::geography) yaklaşık 230 metre.

        double centerLat = 39.920770; // Kızılay
        double centerLon = 32.854110;
        double distanceInMeters = 500.0; // 500 metre

        List<Mall> nearbyMalls = mallRepository.findMallsWithinDistance(centerLat, centerLon, distanceInMeters);

        assertThat(nearbyMalls).isNotNull();
        assertThat(nearbyMalls).hasSize(2);
        assertThat(nearbyMalls).extracting(Mall::getName).containsExactlyInAnyOrder("Mall A Kızılay", "Mall B Sıhhiye");
    }

    @Test
    @DisplayName("Find Malls Within Distance - Should Return Empty List When None Nearby")
    void findMallsWithinDistance_thenReturnEmptyListWhenNoneNearby() {
        // Kızılay (mall1) merkez alınarak 10 metre yarıçap içinde arama (sadece kendini bulmalı ama mesafe çok az)
        // Mesafe çok kısa olduğu için sadece tam merkezdeki (eğer varsa) veya çok yakınındaki bulunmalı.
        // Bu testte, sadece mall1'i bulacak kadar küçük bir mesafe (örn. 1 metre) deneyebiliriz ya da hiçbirini bulamayacak bir nokta seçebiliriz.
        // Ya da çok uzak bir nokta için deneyelim.
        double centerLat = 40.000000; // Kızılay'dan uzak bir nokta
        double centerLon = 33.000000;
        double distanceInMeters = 100.0; // 100 metre

        List<Mall> nearbyMalls = mallRepository.findMallsWithinDistance(centerLat, centerLon, distanceInMeters);

        assertThat(nearbyMalls).isNotNull();
        assertThat(nearbyMalls).isEmpty();
    }

    @Test
    @DisplayName("Find Malls Within Distance - Should Return Only Self If Distance Is Very Small")
    void findMallsWithinDistance_thenReturnOnlySelfIfDistanceIsVerySmall() {
        double centerLat = mall1.getLocation().getY();
        double centerLon = mall1.getLocation().getX();
        double distanceInMeters = 10.0; // 10 metre (sadece kendini kapsamalı)

        List<Mall> nearbyMalls = mallRepository.findMallsWithinDistance(centerLat, centerLon, distanceInMeters);

        assertThat(nearbyMalls).isNotNull();
        assertThat(nearbyMalls).hasSize(1);
        assertThat(nearbyMalls.get(0).getName()).isEqualTo("Mall A Kızılay");
    }
} 