package com.avmerkez.mallservice.service;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.entity.Mall;
import com.avmerkez.mallservice.exception.ResourceNotFoundException;
import com.avmerkez.mallservice.mapper.MallMapper;
import com.avmerkez.mallservice.repository.MallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit 5 için Mockito entegrasyonu
@ActiveProfiles("test") // Test profilini aktif et
class MallServiceImplTest {

    // @Mock: Bu alan için bir Mockito mock nesnesi oluşturur
    @Mock
    private MallRepository mallRepository;

    @Mock
    private MallMapper mallMapper;

    // @InjectMocks: Mock nesnelerini (@Mock ile işaretlenenleri) bu sınıfa enjekte eder
    @InjectMocks
    private MallServiceImpl mallService;

    private Mall mall;
    private MallDto mallDto;
    private CreateMallRequest createMallRequest;
    private UpdateMallRequest updateMallRequest;

    @BeforeEach
    void setUp() {
        // Test verileri hazırlanıyor
        mall = Mall.builder().id(1L).name("Ankara Panora AVM").city("Ankara").district("Çankaya").address("Turan Güneş Bulvarı No:182").build();
        mallDto = new MallDto(1L, "Ankara Panora AVM", "Ankara", "Çankaya", "Turan Güneş Bulvarı No:182", 39.87, 32.85, "10:00-22:00", "https://panora.com.tr", "+90 312 491 2525");
        createMallRequest = new CreateMallRequest("Ankara Panora AVM", "Turan Güneş Bulvarı No:182", "Ankara", "Çankaya", 39.87, 32.85, "10:00-22:00", "https://panora.com.tr", "+90 312 491 2525");
        updateMallRequest = new UpdateMallRequest("Ankara Next Level", "Eskişehir Yolu No:3", "Ankara", "Çankaya", 39.91, 32.78, "09:00-23:00", "https://nextlevel.com.tr", "+90 312 999 9999");
    }

    @Test
    void createMall_ShouldReturnMallDto_WhenSuccessful() {
        // Arrange (Hazırlık)
        // Mapper'ın createRequestToMall metodu çağrıldığında mall nesnesini dönmesini sağla
        when(mallMapper.createRequestToMall(any(CreateMallRequest.class))).thenReturn(mall);
        // Repository'nin save metodu çağrıldığında kaydedilmiş mall nesnesini dönmesini sağla
        when(mallRepository.save(any(Mall.class))).thenReturn(mall);
        // Mapper'ın toMallDto metodu çağrıldığında mallDto nesnesini dönmesini sağla
        when(mallMapper.toMallDto(any(Mall.class))).thenReturn(mallDto);

        // Act (Çalıştırma)
        MallDto result = mallService.createMall(createMallRequest);

        // Assert (Doğrulama)
        assertNotNull(result);
        assertEquals(mallDto.getId(), result.getId());
        assertEquals(mallDto.getName(), result.getName());
        verify(mallRepository, times(1)).save(any(Mall.class)); // save metodunun 1 kez çağrıldığını doğrula
    }

    @Test
    void getMallById_ShouldReturnMallDto_WhenMallExists() {
        // Arrange
        when(mallRepository.findById(anyLong())).thenReturn(Optional.of(mall));
        when(mallMapper.toMallDto(any(Mall.class))).thenReturn(mallDto);

        // Act
        MallDto result = mallService.getMallById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(mallDto.getId(), result.getId());
    }

    @Test
    void getMallById_ShouldThrowResourceNotFoundException_WhenMallDoesNotExist() {
        // Arrange
        when(mallRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            mallService.getMallById(1L);
        });
        verify(mallMapper, never()).toMallDto(any(Mall.class)); // toMallDto'nun hiç çağrılmadığını doğrula
    }

    @Test
    void getAllMalls_ShouldReturnListOfMallDtos() {
        // Arrange
        String cityFilter = "Ankara";
        String districtFilter = "Çankaya";
        List<Mall> mallList = Collections.singletonList(mall);
        List<MallDto> mallDtoList = Collections.singletonList(mallDto);
        when(mallRepository.findAll()).thenReturn(mallList);
        when(mallMapper.toMallDtoList(anyList())).thenReturn(mallDtoList);
        // Act
        List<MallDto> result = mallService.getAllMalls(cityFilter, districtFilter);
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(mallDto.getId(), result.get(0).getId());
    }

    @Test
    void updateMall_ShouldReturnUpdatedMallDto_WhenMallExists() {
        // Arrange
        when(mallRepository.findById(anyLong())).thenReturn(Optional.of(mall));
        // Mapper'ın update metodu çağrıldığında bir şey yapmasına gerek yok (void)
        // doNothing().when(mallMapper).updateMallFromRequest(any(UpdateMallRequest.class), any(Mall.class));
        when(mallRepository.save(any(Mall.class))).thenReturn(mall); // Güncellenmiş mall dönsün
        when(mallMapper.toMallDto(any(Mall.class))).thenReturn(mallDto); // Güncellenmiş DTO dönsün (test için aynı dto'yu kullanıyoruz)

        // Act
        MallDto result = mallService.updateMall(1L, updateMallRequest);

        // Assert
        assertNotNull(result);
        verify(mallMapper).updateMallFromRequest(eq(updateMallRequest), eq(mall)); // Doğru parametrelerle çağrıldığını kontrol et
        verify(mallRepository).save(mall); // Kaydetme işleminin çağrıldığını kontrol et
    }

    @Test
    void updateMall_ShouldThrowResourceNotFoundException_WhenMallDoesNotExist() {
        // Arrange
        when(mallRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            mallService.updateMall(1L, updateMallRequest);
        });
        verify(mallMapper, never()).updateMallFromRequest(any(), any());
        verify(mallRepository, never()).save(any());
    }

    @Test
    void deleteMall_ShouldCallDeleteById_WhenMallExists() {
        // Arrange
        when(mallRepository.existsById(anyLong())).thenReturn(true);
        // deleteById void olduğu için özel bir when() gerekmez, ama çağrıldığını doğrulamalıyız
        doNothing().when(mallRepository).deleteById(anyLong());

        // Act
        mallService.deleteMall(1L);

        // Assert
        verify(mallRepository, times(1)).existsById(1L);
        verify(mallRepository, times(1)).deleteById(1L); // deleteById'ın doğru ID ile çağrıldığını doğrula
    }

    @Test
    void deleteMall_ShouldThrowResourceNotFoundException_WhenMallDoesNotExist() {
        // Arrange
        when(mallRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            mallService.deleteMall(1L);
        });
        verify(mallRepository, never()).deleteById(anyLong()); // deleteById'ın hiç çağrılmadığını doğrula
    }

    @Test
    void findMallsNearLocation_ShouldReturnListOfMallDtos_WhenMallsFound() {
        // Arrange
        double lat = 39.920770;
        double lon = 32.854110;
        double distKm = 5.0;
        double distMeters = distKm * 1000.0;

        List<Mall> foundMalls = Collections.singletonList(mall); // Örnek bir AVM listesi
        List<MallDto> expectedDtos = Collections.singletonList(mallDto); // Eşleşen DTO listesi

        when(mallRepository.findMallsWithinDistance(lat, lon, distMeters)).thenReturn(foundMalls);
        when(mallMapper.toMallDtoList(foundMalls)).thenReturn(expectedDtos);

        // Act
        List<MallDto> result = mallService.findMallsNearLocation(lat, lon, distKm);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(expectedDtos.get(0).getId(), result.get(0).getId());
        verify(mallRepository).findMallsWithinDistance(lat, lon, distMeters);
        verify(mallMapper).toMallDtoList(foundMalls);
    }

    @Test
    void findMallsNearLocation_ShouldReturnEmptyList_WhenNoMallsFound() {
        // Arrange
        double lat = 40.0;
        double lon = 33.0;
        double distKm = 1.0;
        double distMeters = distKm * 1000.0;

        when(mallRepository.findMallsWithinDistance(lat, lon, distMeters)).thenReturn(Collections.emptyList());
        when(mallMapper.toMallDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<MallDto> result = mallService.findMallsNearLocation(lat, lon, distKm);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mallRepository).findMallsWithinDistance(lat, lon, distMeters);
        verify(mallMapper).toMallDtoList(Collections.emptyList());
    }
} 