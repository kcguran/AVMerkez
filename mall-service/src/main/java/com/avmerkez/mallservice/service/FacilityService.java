package com.avmerkez.mallservice.service;

import com.avmerkez.mallservice.dto.CreateFacilityRequest;
import com.avmerkez.mallservice.dto.FacilityDto;
import com.avmerkez.mallservice.dto.UpdateFacilityRequest;

import java.util.List;

public interface FacilityService {
    FacilityDto createFacility(Long mallId, CreateFacilityRequest request);
    FacilityDto updateFacility(Long mallId, Long facilityId, UpdateFacilityRequest request);
    void deleteFacility(Long mallId, Long facilityId);
    List<FacilityDto> getFacilitiesByMall(Long mallId);
    FacilityDto getFacilityById(Long mallId, Long facilityId);
} 