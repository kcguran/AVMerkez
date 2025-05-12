package com.avmerkez.storeservice.mapper;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDetailDto;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, BrandMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StoreMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "brand.id", target = "brandId")
    StoreDto toStoreDto(Store store);

    List<StoreDto> toStoreDtoList(List<Store> stores);

    // For StoreDetailDto, Category and Brand objects are mapped directly by their mappers
    StoreDetailDto toStoreDetailDto(Store store);

    List<StoreDetailDto> toStoreDetailDtoList(List<Store> stores);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true) // Will be handled in service layer
    @Mapping(target = "brand", ignore = true)    // Will be handled in service layer
    Store toStore(CreateStoreRequest createStoreRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true) // Will be handled in service layer
    @Mapping(target = "brand", ignore = true)    // Will be handled in service layer
    void updateStoreFromRequest(UpdateStoreRequest updateStoreRequest, @MappingTarget Store store);
} 