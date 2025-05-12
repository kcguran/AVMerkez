package com.avmerkez.storeservice.service;

import com.avmerkez.storeservice.dto.BrandDto;
import com.avmerkez.storeservice.dto.CreateBrandRequest;
import com.avmerkez.storeservice.dto.UpdateBrandRequest;
import com.avmerkez.storeservice.entity.Brand;
import com.avmerkez.storeservice.exception.DuplicateEntityException;
import com.avmerkez.storeservice.exception.EntityNotFoundException;
import com.avmerkez.storeservice.mapper.BrandMapper;
import com.avmerkez.storeservice.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Transactional
    public BrandDto createBrand(CreateBrandRequest createBrandRequest) {
        brandRepository.findByName(createBrandRequest.getName()).ifPresent(b -> {
            throw new DuplicateEntityException("Brand with name '" + createBrandRequest.getName() + "' already exists.");
        });
        Brand brand = brandMapper.toBrand(createBrandRequest);
        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toBrandDto(savedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto getBrandById(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id: " + brandId));
        return brandMapper.toBrandDto(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> getAllBrands() {
        return brandMapper.toBrandDtoList(brandRepository.findAll());
    }

    @Override
    @Transactional
    public BrandDto updateBrand(Long brandId, UpdateBrandRequest updateBrandRequest) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id: " + brandId));

        if (updateBrandRequest.getName() != null && !updateBrandRequest.getName().equals(brand.getName())) {
            brandRepository.findByName(updateBrandRequest.getName()).ifPresent(b -> {
                throw new DuplicateEntityException("Brand with name '" + updateBrandRequest.getName() + "' already exists.");
            });
        }

        brandMapper.updateBrandFromRequest(updateBrandRequest, brand);
        Brand updatedBrand = brandRepository.save(brand);
        return brandMapper.toBrandDto(updatedBrand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long brandId) {
        if (!brandRepository.existsById(brandId)) {
            throw new EntityNotFoundException("Brand not found with id: " + brandId);
        }
        // TODO: Add logic to handle stores referencing this brand before deletion
        brandRepository.deleteById(brandId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean brandExists(Long brandId) {
        return brandRepository.existsById(brandId);
    }
} 