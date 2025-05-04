package com.avmerkez.mallservice.mapper;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.entity.Mall;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

// componentModel = "spring" -> Spring Bean olarak tanımlanmasını sağlar
// unmappedTargetPolicy = ReportingPolicy.IGNORE -> Hedefte olmayan alanlar için hata vermez
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Null değerler maplenmez (update için önemli)
public interface MallMapper {

    MallDto toMallDto(Mall mall);

    List<MallDto> toMallDtoList(List<Mall> malls);

    Mall createRequestToMall(CreateMallRequest createMallRequest);

    // Var olan bir Mall nesnesini UpdateMallRequest ile günceller
    // Null gelen alanlar IGNORE edildiği için sadece dolu gelenler güncellenir
    void updateMallFromRequest(UpdateMallRequest updateMallRequest, @MappingTarget Mall mall);
} 