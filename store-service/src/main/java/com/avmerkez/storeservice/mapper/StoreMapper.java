package com.avmerkez.storeservice.mapper;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StoreMapper {

    StoreDto toStoreDto(Store store);

    List<StoreDto> toStoreDtoList(List<Store> stores);

    Store createRequestToStore(CreateStoreRequest createStoreRequest);

    void updateStoreFromRequest(UpdateStoreRequest updateStoreRequest, @MappingTarget Store store);
} 