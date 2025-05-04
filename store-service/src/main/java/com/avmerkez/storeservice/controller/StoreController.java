package com.avmerkez.storeservice.controller;

import com.avmerkez.storeservice.dto.CreateStoreRequest;
import com.avmerkez.storeservice.dto.StoreDto;
import com.avmerkez.storeservice.dto.UpdateStoreRequest;
import com.avmerkez.storeservice.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores") // Temel path
@RequiredArgsConstructor
@Tag(name = "Store API", description = "Operations pertaining to stores in AVMerkez")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @Operation(summary = "Create a new store")
    @ApiResponse(responseCode = "201", description = "Store created successfully")
    public ResponseEntity<StoreDto> createStore(@Valid @RequestBody CreateStoreRequest createStoreRequest) {
        StoreDto createdStore = storeService.createStore(createStoreRequest);
        return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a store by its id")
    @ApiResponse(responseCode = "200", description = "Found the store")
    @ApiResponse(responseCode = "404", description = "Store not found")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id) {
        StoreDto storeDto = storeService.getStoreById(id);
        return ResponseEntity.ok(storeDto);
    }

    @GetMapping
    @Operation(summary = "Get all stores, optionally filtered by mallId")
    @ApiResponse(responseCode = "200", description = "List of stores")
    public ResponseEntity<List<StoreDto>> getAllStores(
            @Parameter(description = "Filter stores by Mall ID")
            @RequestParam(required = false) Long mallId) {
        List<StoreDto> stores = storeService.getAllStores(mallId);
        return ResponseEntity.ok(stores);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing store")
    @ApiResponse(responseCode = "200", description = "Store updated successfully")
    @ApiResponse(responseCode = "404", description = "Store not found")
    public ResponseEntity<StoreDto> updateStore(@PathVariable Long id, @Valid @RequestBody UpdateStoreRequest updateStoreRequest) {
        StoreDto updatedStore = storeService.updateStore(id, updateStoreRequest);
        return ResponseEntity.ok(updatedStore);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a store by its id")
    @ApiResponse(responseCode = "204", description = "Store deleted successfully")
    @ApiResponse(responseCode = "404", description = "Store not found")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    // Not: /malls/{mallId}/stores endpoint'i API Gateway seviyesinde bu controller'ın
    // getAllStores metoduna (mallId parametresi ile) yönlendirilebilir.
} 