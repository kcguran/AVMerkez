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
        // Her testten önce kullanılacak örnek verileri hazırla
        mall = Mall.builder().id(1L).name("Test Mall").city("Test City").district("Test District").address("Test Address").build();
        mallDto = new MallDto(1L, "Test Mall", "Test City", "Test District");
        createMallRequest = new CreateMallRequest("Test Mall", "Test Address", "Test City", "Test District");
        updateMallRequest = new UpdateMallRequest("Updated Mall", "Updated Address", "Updated City", "Updated District");
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
        String cityFilter = null; // veya test için bir değer
        String districtFilter = null; // veya test için bir değer
        List<Mall> mallList = Collections.singletonList(mall);
        List<MallDto> mallDtoList = Collections.singletonList(mallDto);
        // TODO: Depoya filtre parametreleri eklendiğinde mock'u güncelle
        when(mallRepository.findAll()).thenReturn(mallList);
        when(mallMapper.toMallDtoList(anyList())).thenReturn(mallDtoList);

        // Act
        List<MallDto> result = mallService.getAllMalls(cityFilter, districtFilter); // Parametreler eklendi

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
} 