package com.avmerkez.mallservice.controller;

import com.avmerkez.mallservice.dto.CreateFacilityRequest;
import com.avmerkez.mallservice.dto.FacilityDto;
import com.avmerkez.mallservice.dto.UpdateFacilityRequest;
import com.avmerkez.mallservice.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/malls/{mallId}/facilities")
@RequiredArgsConstructor
@Tag(name = "AVM Hizmetleri (Facilities)", description = "AVM'ye ait hizmetlerin yönetimi")
public class FacilityController {
    private final FacilityService facilityService;

    @Operation(summary = "AVM'ye yeni hizmet ekle")
    @PostMapping
    public ResponseEntity<FacilityDto> createFacility(@PathVariable Long mallId,
                                                     @Valid @RequestBody CreateFacilityRequest request) {
        return ResponseEntity.ok(facilityService.createFacility(mallId, request));
    }

    @Operation(summary = "AVM'deki hizmetleri listele")
    @GetMapping
    public ResponseEntity<List<FacilityDto>> getFacilities(@PathVariable Long mallId) {
        return ResponseEntity.ok(facilityService.getFacilitiesByMall(mallId));
    }

    @Operation(summary = "AVM'de belirli bir hizmeti getir")
    @GetMapping("/{facilityId}")
    public ResponseEntity<FacilityDto> getFacility(@PathVariable Long mallId, @PathVariable Long facilityId) {
        return ResponseEntity.ok(facilityService.getFacilityById(mallId, facilityId));
    }

    @Operation(summary = "AVM'deki hizmeti güncelle")
    @PutMapping("/{facilityId}")
    public ResponseEntity<FacilityDto> updateFacility(@PathVariable Long mallId,
                                                     @PathVariable Long facilityId,
                                                     @Valid @RequestBody UpdateFacilityRequest request) {
        return ResponseEntity.ok(facilityService.updateFacility(mallId, facilityId, request));
    }

    @Operation(summary = "AVM'den hizmeti sil")
    @DeleteMapping("/{facilityId}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Long mallId, @PathVariable Long facilityId) {
        facilityService.deleteFacility(mallId, facilityId);
        return ResponseEntity.noContent().build();
    }
} 