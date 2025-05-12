package com.avmerkez.storeservice.controller;

import com.avmerkez.storeservice.dto.BrandDto;
import com.avmerkez.storeservice.dto.CreateBrandRequest;
import com.avmerkez.storeservice.dto.ErrorResponse;
import com.avmerkez.storeservice.dto.UpdateBrandRequest;
import com.avmerkez.storeservice.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(name = "Brand API", description = "API for managing product brands")
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "Create a new brand", description = "Creates a new product brand. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Brand created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BrandDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Brand already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@Valid @RequestBody CreateBrandRequest createBrandRequest) {
        BrandDto createdBrand = brandService.createBrand(createBrandRequest);
        return new ResponseEntity<>(createdBrand, HttpStatus.CREATED);
    }

    @Operation(summary = "Get brand by ID", description = "Retrieves a specific brand by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BrandDto.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{brandId}")
    public ResponseEntity<BrandDto> getBrandById(
            @Parameter(description = "ID of the brand to retrieve", required = true) @PathVariable Long brandId) {
        BrandDto brandDto = brandService.getBrandById(brandId);
        return ResponseEntity.ok(brandDto);
    }

    @Operation(summary = "Get all brands", description = "Retrieves a list of all product brands.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of brands retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))) // Further schema detail for List<BrandDto> might be needed
    })
    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        List<BrandDto> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @Operation(summary = "Update an existing brand", description = "Updates an existing brand by its ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BrandDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Brand name conflict after update",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{brandId}")
    public ResponseEntity<BrandDto> updateBrand(
            @Parameter(description = "ID of the brand to update", required = true) @PathVariable Long brandId,
            @Valid @RequestBody UpdateBrandRequest updateBrandRequest) {
        BrandDto updatedBrand = brandService.updateBrand(brandId, updateBrandRequest);
        return ResponseEntity.ok(updatedBrand);
    }

    @Operation(summary = "Delete a brand", description = "Deletes a brand by its ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Brand deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{brandId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBrand(
            @Parameter(description = "ID of the brand to delete", required = true) @PathVariable Long brandId) {
        brandService.deleteBrand(brandId);
    }
} 