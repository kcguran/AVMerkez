package com.avmerkez.campaigneventservice.repository;

import com.avmerkez.campaigneventservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByMallId(Long mallId);

    // Find active events for a specific mall
    List<Event> findByMallIdAndStartDateBeforeAndEndDateAfter(Long mallId, LocalDateTime currentTime1, LocalDateTime currentTime2);

    // Find upcoming events for a specific mall
    List<Event> findByMallIdAndStartDateAfter(Long mallId, LocalDateTime currentTime);

} 