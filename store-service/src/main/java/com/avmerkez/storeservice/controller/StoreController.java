package com.avmerkez.storeservice.controller;

import com.avmerkez.storeservice.dto.*;
import com.avmerkez.storeservice.service.StoreService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Tag(name = "Store API", description = "API for managing stores within malls")
public class StoreController {

    private final StoreService storeService;
    private static final Logger log = LoggerFactory.getLogger(StoreController.class);

    @Operation(summary = "Create a new store", description = "Creates a new store. Requires ADMIN or MALL_MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Store created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StoreDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., validation error, invalid mallId, categoryId, or brandId)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category or Brand not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Mall service unavailable (if mallId validation fails due to service outage)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<StoreDetailDto> createStore(@Valid @RequestBody CreateStoreRequest createStoreRequest) {
        StoreDetailDto createdStore = storeService.createStore(createStoreRequest);
        return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
    }

    @Operation(summary = "Get store by ID", description = "Retrieves detailed information for a specific store by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Store found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StoreDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "Store not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailDto> getStoreById(
            @Parameter(description = "ID of the store to retrieve", required = true) @PathVariable Long storeId) {
        StoreDetailDto storeDto = storeService.getStoreById(storeId);
        return ResponseEntity.ok(storeDto);
    }

    @RequestMapping(method = RequestMethod.HEAD, value = "/{storeId}/exists")
    @Operation(summary = "Check if a store exists by its ID",
               description = "Returns a 200 OK if the store exists, or 404 Not Found if it does not.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Store exists"),
            @ApiResponse(responseCode = "404", description = "Store not found")
    })
    public ResponseEntity<Void> checkStoreExists(@PathVariable Long storeId) {
        log.debug("HEAD request to check existence of store with id: {}", storeId);
        if (storeService.checkStoreExists(storeId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get a list of stores", description = "Retrieves a list of stores (basic DTO). Can be filtered by mallId, categoryId, or brandId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of stores retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))), // Further schema detail for List<StoreDto> might be needed
            @ApiResponse(responseCode = "404", description = "Category or Brand not found if filtering by non-existent ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<StoreDto>> getStores(
            @Parameter(description = "Filter stores by Mall ID") @RequestParam(required = false) Long mallId,
            @Parameter(description = "Filter stores by Category ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter stores by Brand ID") @RequestParam(required = false) Long brandId) {

        List<StoreDto> stores;
        if (mallId != null && categoryId != null) {
            stores = storeService.getStoresByMallAndCategoryId(mallId, categoryId);
        } else if (mallId != null && brandId != null) {
            stores = storeService.getStoresByMallAndBrandId(mallId, brandId);
        } else if (mallId != null) {
            stores = storeService.getStoresByMallId(mallId);
        } else if (categoryId != null) {
            stores = storeService.getStoresByCategoryId(categoryId);
        } else if (brandId != null) {
            stores = storeService.getStoresByBrandId(brandId);
        } else {
            stores = storeService.getAllStores();
        }
        return ResponseEntity.ok(stores);
    }

    @Operation(summary = "Get all stores with detailed information", description = "Retrieves a list of all stores with detailed DTOs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of detailed stores retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))) // Further schema detail for List<StoreDetailDto> might be needed
    })
    @GetMapping("/detailed")
    public ResponseEntity<List<StoreDetailDto>> getAllStoresDetailed() {
        List<StoreDetailDto> stores = storeService.getAllStoresDetail();
        return ResponseEntity.ok(stores);
    }

    @Operation(summary = "Update an existing store", description = "Updates an existing store by its ID. Requires ADMIN or MALL_MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Store updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StoreDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., validation error, invalid mallId, categoryId, or brandId)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Store, Category, or Brand not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Mall service unavailable (if mallId validation fails due to service outage)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreDetailDto> updateStore(
            @Parameter(description = "ID of the store to update", required = true) @PathVariable Long storeId,
            @Valid @RequestBody UpdateStoreRequest updateStoreRequest) {
        StoreDetailDto updatedStore = storeService.updateStore(storeId, updateStoreRequest);
        return ResponseEntity.ok(updatedStore);
    }

    @Operation(summary = "Delete a store", description = "Deletes a store by its ID. Requires ADMIN or MALL_MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Store deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Store not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{storeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStore(
            @Parameter(description = "ID of the store to delete", required = true) @PathVariable Long storeId) {
        storeService.deleteStore(storeId);
    }
} 