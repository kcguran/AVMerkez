package com.avmerkez.campaigneventservice.controller;

import com.avmerkez.campaigneventservice.dto.CampaignDto;
import com.avmerkez.campaigneventservice.dto.CreateCampaignRequest;
import com.avmerkez.campaigneventservice.dto.UpdateCampaignRequest;
import com.avmerkez.campaigneventservice.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Tag(name = "Campaign API", description = "API for managing campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MALL_MANAGER')")
    @Operation(summary = "Create a new campaign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Campaign created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<CampaignDto> createCampaign(@Valid @RequestBody CreateCampaignRequest request) {
        CampaignDto createdCampaign = campaignService.createCampaign(request);
        return new ResponseEntity<>(createdCampaign, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get campaign by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign found"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    public ResponseEntity<CampaignDto> getCampaignById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    @GetMapping
    @Operation(summary = "Get all campaigns or filter active ones")
    public ResponseEntity<List<CampaignDto>> getCampaigns(@RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<CampaignDto> campaigns = activeOnly ? campaignService.getActiveCampaigns() : campaignService.getAllCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/mall/{mallId}")
    @Operation(summary = "Get active campaigns for a specific mall")
    public ResponseEntity<List<CampaignDto>> getActiveCampaignsByMall(@PathVariable Long mallId) {
        return ResponseEntity.ok(campaignService.getActiveCampaignsByMallId(mallId));
    }

    @GetMapping("/store/{storeId}")
     @Operation(summary = "Get active campaigns for a specific store")
    public ResponseEntity<List<CampaignDto>> getActiveCampaignsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(campaignService.getActiveCampaignsByStoreId(storeId));
    }
    
    @GetMapping("/brand/{brandId}")
    @Operation(summary = "Get active campaigns for a specific brand")
    public ResponseEntity<List<CampaignDto>> getActiveCampaignsByBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(campaignService.getActiveCampaignsByBrandId(brandId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MALL_MANAGER')")
    @Operation(summary = "Update an existing campaign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    public ResponseEntity<CampaignDto> updateCampaign(@PathVariable Long id, @Valid @RequestBody UpdateCampaignRequest request) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MALL_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a campaign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Campaign deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    public void deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
    }
} 