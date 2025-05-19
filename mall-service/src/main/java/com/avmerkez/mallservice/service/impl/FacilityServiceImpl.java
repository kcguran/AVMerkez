package com.avmerkez.mallservice.service.impl;

import com.avmerkez.mallservice.dto.CreateFacilityRequest;
import com.avmerkez.mallservice.dto.FacilityDto;
import com.avmerkez.mallservice.dto.UpdateFacilityRequest;
import com.avmerkez.mallservice.entity.Facility;
import com.avmerkez.mallservice.entity.Mall;
import com.avmerkez.mallservice.exception.ResourceNotFoundException;
import com.avmerkez.mallservice.mapper.FacilityMapper;
import com.avmerkez.mallservice.repository.FacilityRepository;
import com.avmerkez.mallservice.repository.MallRepository;
import com.avmerkez.mallservice.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;
    private final MallRepository mallRepository;
    private final FacilityMapper facilityMapper;

    @Override
    public FacilityDto createFacility(Long mallId, CreateFacilityRequest request) {
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new ResourceNotFoundException("Mall", "id", mallId));
        Facility facility = facilityMapper.createRequestToFacility(request);
        facility.setMall(mall);
        Facility saved = facilityRepository.save(facility);
        return facilityMapper.toFacilityDto(saved);
    }

    @Override
    public FacilityDto updateFacility(Long mallId, Long facilityId, UpdateFacilityRequest request) {
        Facility facility = getFacilityEntity(mallId, facilityId);
        facilityMapper.updateFacilityFromRequest(request, facility);
        Facility updated = facilityRepository.save(facility);
        return facilityMapper.toFacilityDto(updated);
    }

    @Override
    public void deleteFacility(Long mallId, Long facilityId) {
        Facility facility = getFacilityEntity(mallId, facilityId);
        facilityRepository.delete(facility);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacilityDto> getFacilitiesByMall(Long mallId) {
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new ResourceNotFoundException("Mall", "id", mallId));
        List<Facility> facilities = facilityRepository.findByMall(mall);
        return facilityMapper.toFacilityDtoList(facilities);
    }

    @Override
    @Transactional(readOnly = true)
    public FacilityDto getFacilityById(Long mallId, Long facilityId) {
        Facility facility = getFacilityEntity(mallId, facilityId);
        return facilityMapper.toFacilityDto(facility);
    }

    private Facility getFacilityEntity(Long mallId, Long facilityId) {
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new ResourceNotFoundException("Mall", "id", mallId));
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", "id", facilityId));
        if (!facility.getMall().getId().equals(mall.getId())) {
            throw new ResourceNotFoundException("Facility", "mallId", mallId);
        }
        return facility;
    }
} 