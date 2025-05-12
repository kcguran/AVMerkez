package com.avmerkez.storeservice.mapper;

import com.avmerkez.storeservice.dto.BrandDto;
import com.avmerkez.storeservice.dto.CreateBrandRequest;
import com.avmerkez.storeservice.dto.UpdateBrandRequest;
import com.avmerkez.storeservice.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BrandMapper {

    BrandDto toBrandDto(Brand brand);

    List<BrandDto> toBrandDtoList(List<Brand> brands);

    @Mapping(target = "id", ignore = true)
    Brand toBrand(CreateBrandRequest createBrandRequest);

    @Mapping(target = "id", ignore = true)
    void updateBrandFromRequest(UpdateBrandRequest updateBrandRequest, @MappingTarget Brand brand);
} 