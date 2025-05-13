package com.avmerkez.campaigneventservice.controller;

import com.avmerkez.campaigneventservice.dto.CreateEventRequest;
import com.avmerkez.campaigneventservice.dto.EventDto;
import com.avmerkez.campaigneventservice.dto.UpdateEventRequest;
import com.avmerkez.campaigneventservice.service.EventService;
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
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Event API", description = "API for managing events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MALL_MANAGER')")
    @Operation(summary = "Create a new event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Event created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody CreateEventRequest request) {
        EventDto createdEvent = eventService.createEvent(request);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
     @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/mall/{mallId}")
    @Operation(summary = "Get events for a specific mall", 
               description = "Optionally filter by status: active, upcoming, or all (default)")
    public ResponseEntity<List<EventDto>> getEventsByMall(
            @PathVariable Long mallId,
            @RequestParam(required = false) String status) { // active, upcoming
        
        List<EventDto> events;
        if ("active".equalsIgnoreCase(status)) {
            events = eventService.getActiveEventsByMallId(mallId);
        } else if ("upcoming".equalsIgnoreCase(status)) {
            events = eventService.getUpcomingEventsByMallId(mallId);
        } else {
            events = eventService.getEventsByMallId(mallId);
        }
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MALL_MANAGER')")
    @Operation(summary = "Update an existing event")
     @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @Valid @RequestBody UpdateEventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MALL_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
} 