package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.BrandDto;
import com.avmerkez.storeservice.dto.CreateBrandRequest;
import com.avmerkez.storeservice.dto.UpdateBrandRequest;

import java.util.List;

public interface BrandService {
    BrandDto createBrand(CreateBrandRequest createBrandRequest);
    BrandDto getBrandById(Long brandId);
    List<BrandDto> getAllBrands();
    BrandDto updateBrand(Long brandId, UpdateBrandRequest updateBrandRequest);
    void deleteBrand(Long brandId);
    boolean brandExists(Long brandId);
} 