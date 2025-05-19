package com.avmerkez.mallservice.service;

import com.avmerkez.mallservice.event.MallCreatedEvent;

public interface KafkaProducerService {
    void sendMallCreatedEvent(MallCreatedEvent event);
} 