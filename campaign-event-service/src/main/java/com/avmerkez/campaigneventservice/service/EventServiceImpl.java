package com.avmerkez.campaigneventservice.service;

import com.avmerkez.campaigneventservice.dto.CreateEventRequest;
import com.avmerkez.campaigneventservice.dto.EventDto;
import com.avmerkez.campaigneventservice.dto.UpdateEventRequest;
import com.avmerkez.campaigneventservice.entity.Event;
import com.avmerkez.campaigneventservice.exception.ResourceNotFoundException; // Need to create this exception
import com.avmerkez.campaigneventservice.mapper.EventMapper;
import com.avmerkez.campaigneventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    // private final MallServiceClient mallServiceClient; // Inject when validation is needed

    @Override
    @Transactional
    public EventDto createEvent(CreateEventRequest request) {
        log.info("Creating new event: {}", request.getName());
        // TODO: Add validation for mallId using Feign client
        // validateMallExists(request.getMallId());

        Event event = eventMapper.toEntity(request);
        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with id: {}", savedEvent.getId());
        return eventMapper.toDto(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(Long id) {
        log.debug("Fetching event with id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByMallId(Long mallId) {
        log.debug("Fetching all events for mallId: {}", mallId);
        // Optional: Validate mallId exists here too?
        return eventMapper.toDto(eventRepository.findByMallId(mallId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getActiveEventsByMallId(Long mallId) {
        log.debug("Fetching active events for mallId: {}", mallId);
        LocalDateTime now = LocalDateTime.now();
        return eventMapper.toDto(eventRepository.findByMallIdAndStartDateBeforeAndEndDateAfter(mallId, now, now));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getUpcomingEventsByMallId(Long mallId) {
        log.debug("Fetching upcoming events for mallId: {}", mallId);
        LocalDateTime now = LocalDateTime.now();
        return eventMapper.toDto(eventRepository.findByMallIdAndStartDateAfter(mallId, now));
    }

    @Override
    @Transactional
    public EventDto updateEvent(Long id, UpdateEventRequest request) {
        log.info("Updating event with id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        // TODO: Add validation if mallId is being updated

        eventMapper.updateEntityFromRequest(request, event);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated successfully with id: {}", updatedEvent.getId());
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        log.info("Deleting event with id: {}", id);
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", "id", id);
        }
        eventRepository.deleteById(id);
        log.info("Event deleted successfully with id: {}", id);
    }

    // Placeholder for validation logic
    // private void validateMallExists(Long mallId) {
    //    if (mallId != null) { /* Call mallServiceClient.checkMallExists(mallId); */ }
    //    else { throw new InvalidInputException("Mall ID cannot be null for an event"); }
    //}
} 