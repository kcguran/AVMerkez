package com.avmerkez.campaigneventservice.service;

import com.avmerkez.campaigneventservice.dto.CreateEventRequest;
import com.avmerkez.campaigneventservice.dto.EventDto;
import com.avmerkez.campaigneventservice.dto.UpdateEventRequest;

import java.util.List;

public interface EventService {

    EventDto createEvent(CreateEventRequest request);

    EventDto getEventById(Long id);

    List<EventDto> getEventsByMallId(Long mallId);

    List<EventDto> getActiveEventsByMallId(Long mallId);

    List<EventDto> getUpcomingEventsByMallId(Long mallId);

    EventDto updateEvent(Long id, UpdateEventRequest request);

    void deleteEvent(Long id);
} 