package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.BrandDto;
import com.avmerkez.storeservice.dto.CreateBrandRequest;
import com.avmerkez.storeservice.dto.UpdateBrandRequest;
import com.avmerkez.storeservice.entity.Brand;
import com.avmerkez.storeservice.exception.DuplicateEntityException;
import com.avmerkez.storeservice.exception.EntityNotFoundException;
import com.avmerkez.storeservice.mapper.BrandMapper;
import com.avmerkez.storeservice.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandMapper brandMapper;

    @InjectMocks
    private BrandServiceImpl brandService;

    private Brand brand;
    private BrandDto brandDto;
    private CreateBrandRequest createRequest;
    private UpdateBrandRequest updateRequest;

    @BeforeEach
    void setUp() {
        brand = new Brand("SuperBrand");
        brand.setId(1L);
        brand.setLogoUrl("logo.png");
        brand.setWebsite("superbrand.com");

        brandDto = new BrandDto(1L, "SuperBrand", "logo.png", "superbrand.com");

        createRequest = new CreateBrandRequest();
        createRequest.setName("SuperBrand");
        createRequest.setLogoUrl("logo.png");
        // createRequest.setWebsite("superbrand.com"); // Comment out if method doesn't exist

        updateRequest = new UpdateBrandRequest();
        updateRequest.setName("MegaBrand");
    }

    @Test
    @DisplayName("createBrand should save and return BrandDto")
    void createBrand_ValidRequest_ReturnsBrandDto() {
        when(brandRepository.findByName("SuperBrand")).thenReturn(Optional.empty());
        when(brandMapper.toBrand(createRequest)).thenReturn(brand);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        when(brandMapper.toBrandDto(brand)).thenReturn(brandDto);

        BrandDto result = brandService.createBrand(createRequest);

        assertThat(result).isEqualTo(brandDto);
        verify(brandRepository).save(brand);
    }

    @Test
    @DisplayName("createBrand with existing name should throw DuplicateEntityException")
    void createBrand_ExistingName_ThrowsDuplicateEntityException() {
        when(brandRepository.findByName("SuperBrand")).thenReturn(Optional.of(brand));

        assertThrows(DuplicateEntityException.class, () -> brandService.createBrand(createRequest));
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    @DisplayName("getBrandById should return DTO when found")
    void getBrandById_Exists_ReturnsBrandDto() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandMapper.toBrandDto(brand)).thenReturn(brandDto);

        BrandDto result = brandService.getBrandById(1L);

        assertThat(result).isEqualTo(brandDto);
    }

    @Test
    @DisplayName("getBrandById should throw EntityNotFoundException when not found")
    void getBrandById_NotExists_ThrowsEntityNotFoundException() {
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.getBrandById(1L));
    }

    @Test
    @DisplayName("updateBrand should update and return DTO")
    void updateBrand_ValidRequest_ReturnsUpdatedBrandDto() {
        Brand updatedEntity = new Brand("MegaBrand");
        updatedEntity.setId(1L);
        BrandDto updatedDto = new BrandDto(1L, "MegaBrand", null, null);

        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.findByName("MegaBrand")).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenReturn(updatedEntity);
        when(brandMapper.toBrandDto(updatedEntity)).thenReturn(updatedDto);
        doNothing().when(brandMapper).updateBrandFromRequest(updateRequest, brand);

        BrandDto result = brandService.updateBrand(1L, updateRequest);

        verify(brandMapper).updateBrandFromRequest(updateRequest, brand);
        verify(brandRepository).save(brand);
        assertThat(result.getName()).isEqualTo("MegaBrand");
    }

    @Test
    @DisplayName("deleteBrand should call deleteById when exists")
    void deleteBrand_Exists_CallsDeleteById() {
        when(brandRepository.existsById(1L)).thenReturn(true);
        doNothing().when(brandRepository).deleteById(1L);

        brandService.deleteBrand(1L);

        verify(brandRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteBrand should throw EntityNotFoundException when not exists")
    void deleteBrand_NotExists_ThrowsEntityNotFoundException() {
        when(brandRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> brandService.deleteBrand(1L));
        verify(brandRepository, never()).deleteById(anyLong());
    }
} 