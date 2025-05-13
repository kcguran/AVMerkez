package com.avmerkez.campaigneventservice.mapper;

import com.avmerkez.campaigneventservice.dto.CreateEventRequest;
import com.avmerkez.campaigneventservice.dto.EventDto;
import com.avmerkez.campaigneventservice.dto.UpdateEventRequest;
import com.avmerkez.campaigneventservice.entity.Event;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> events);

    Event toEntity(CreateEventRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateEventRequest request, @MappingTarget Event event);
} 