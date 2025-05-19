package com.avmerkez.mallservice.mapper;

import com.avmerkez.mallservice.dto.CreateFacilityRequest;
import com.avmerkez.mallservice.dto.FacilityDto;
import com.avmerkez.mallservice.dto.UpdateFacilityRequest;
import com.avmerkez.mallservice.entity.Facility;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    FacilityDto toFacilityDto(Facility facility);
    List<FacilityDto> toFacilityDtoList(List<Facility> facilities);
    Facility createRequestToFacility(CreateFacilityRequest request);
    void updateFacilityFromRequest(UpdateFacilityRequest request, @MappingTarget Facility facility);
} 