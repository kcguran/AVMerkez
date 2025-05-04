package com.avmerkez.mallservice.controller;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.service.MallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/malls") // Temel path (Gateway'den gelen /api/v1/malls buraya maplenecek)
@RequiredArgsConstructor
@Tag(name = "Mall API", description = "Operations pertaining to malls in AVMerkez")
public class MallController {

    private final MallService mallService;

    @PostMapping
    @Operation(summary = "Create a new mall")
    @ApiResponse(responseCode = "201", description = "Mall created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<MallDto> createMall(@Valid @RequestBody CreateMallRequest createMallRequest) {
        MallDto createdMall = mallService.createMall(createMallRequest);
        // Oluşturulan kaynağın URI'sini oluştur
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // Mevcut istek URI'si (/malls)
                .path("/{id}")        // ID path variable'ını ekle
                .buildAndExpand(createdMall.getId()) // ID değerini yerine koy
                .toUri();             // URI nesnesine çevir

        return ResponseEntity.created(location).body(createdMall);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a mall by its id")
    @ApiResponse(responseCode = "200", description = "Found the mall")
    @ApiResponse(responseCode = "404", description = "Mall not found")
    public ResponseEntity<MallDto> getMallById(@PathVariable Long id) {
        MallDto mallDto = mallService.getMallById(id);
        return ResponseEntity.ok(mallDto);
    }

    @GetMapping
    @Operation(summary = "Get all malls")
    @ApiResponse(responseCode = "200", description = "List of malls")
    public ResponseEntity<List<MallDto>> getAllMalls() {
        List<MallDto> malls = mallService.getAllMalls();
        return ResponseEntity.ok(malls);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing mall")
    @ApiResponse(responseCode = "200", description = "Mall updated successfully")
    @ApiResponse(responseCode = "404", description = "Mall not found")
    public ResponseEntity<MallDto> updateMall(@PathVariable Long id, @Valid @RequestBody UpdateMallRequest updateMallRequest) {
        MallDto updatedMall = mallService.updateMall(id, updateMallRequest);
        return ResponseEntity.ok(updatedMall);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mall by its id")
    @ApiResponse(responseCode = "204", description = "Mall deleted successfully")
    @ApiResponse(responseCode = "404", description = "Mall not found")
    public ResponseEntity<Void> deleteMall(@PathVariable Long id) {
        mallService.deleteMall(id);
        return ResponseEntity.noContent().build();
    }
} 