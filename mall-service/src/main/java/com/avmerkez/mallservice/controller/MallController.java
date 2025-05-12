package com.avmerkez.mallservice.controller;

import com.avmerkez.mallservice.dto.CreateMallRequest;
import com.avmerkez.mallservice.dto.GenericApiResponse;
import com.avmerkez.mallservice.dto.MallDto;
import com.avmerkez.mallservice.dto.UpdateMallRequest;
import com.avmerkez.mallservice.service.MallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/malls")
@RequiredArgsConstructor
@Tag(name = "Mall Management", description = "APIs for managing malls")
@Validated
public class MallController {

    private final MallService mallService;

    @PostMapping
    @Operation(summary = "Create a new mall")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Mall created successfully",
            content = @Content(schema = @Schema(implementation = GenericApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = com.avmerkez.mallservice.exception.ErrorResponse.class)))
    public ResponseEntity<GenericApiResponse<MallDto>> createMall(@Valid @RequestBody CreateMallRequest createMallRequest) {
        MallDto createdMall = mallService.createMall(createMallRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdMall.getId())
                .toUri();
        return ResponseEntity.created(location).body(GenericApiResponse.success(createdMall, "Mall created successfully."));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a mall by its id")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found the mall",
            content = @Content(schema = @Schema(implementation = GenericApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mall not found",
            content = @Content(schema = @Schema(implementation = com.avmerkez.mallservice.exception.ErrorResponse.class)))
    public ResponseEntity<GenericApiResponse<MallDto>> getMallById(@PathVariable Long id) {
        MallDto mallDto = mallService.getMallById(id);
        return ResponseEntity.ok(GenericApiResponse.success(mallDto));
    }

    @GetMapping
    @Operation(summary = "Get all malls")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of malls",
            content = @Content(schema = @Schema(implementation = GenericApiResponse.class)))
    public ResponseEntity<GenericApiResponse<List<MallDto>>> getAllMalls(@RequestParam(required = false) String city, @RequestParam(required = false) String district) {
        List<MallDto> malls = mallService.getAllMalls(city, district);
        return ResponseEntity.ok(GenericApiResponse.success(malls));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing mall")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Mall updated successfully",
            content = @Content(schema = @Schema(implementation = GenericApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mall not found",
            content = @Content(schema = @Schema(implementation = com.avmerkez.mallservice.exception.ErrorResponse.class)))
    public ResponseEntity<GenericApiResponse<MallDto>> updateMall(@PathVariable Long id, @Valid @RequestBody UpdateMallRequest updateMallRequest) {
        MallDto updatedMall = mallService.updateMall(id, updateMallRequest);
        return ResponseEntity.ok(GenericApiResponse.success(updatedMall, "Mall updated successfully."));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mall by ID", description = "Deletes a specific mall based on its ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Mall deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mall not found", content = @Content(schema = @Schema(implementation = com.avmerkez.mallservice.exception.ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteMall(@PathVariable Long id) {
        mallService.deleteMall(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/near")
    @Operation(summary = "Find malls near a location",
                 description = "Finds malls within a specified distance (in kilometers) from a given latitude and longitude.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list of nearby malls",
                         content = @Content(schema = @Schema(implementation = GenericApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input parameters",
                         content = @Content(schema = @Schema(implementation = com.avmerkez.mallservice.exception.ErrorResponse.class)))
    })
    public ResponseEntity<GenericApiResponse<List<MallDto>>> findMallsNearLocation(
            @Parameter(description = "Latitude of the center point", required = true, example = "39.92077")
            @RequestParam @Min(-90) @Max(90) double latitude,
            @Parameter(description = "Longitude of the center point", required = true, example = "32.85411")
            @RequestParam @Min(-180) @Max(180) double longitude,
            @Parameter(description = "Search radius in kilometers", required = true, example = "5.0")
            @RequestParam @Positive double distance) {
        List<MallDto> malls = mallService.findMallsNearLocation(latitude, longitude, distance);
        return ResponseEntity.ok(GenericApiResponse.success(malls));
    }
} 