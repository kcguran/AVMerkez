package com.avmerkez.notificationservice.listener;

import com.avmerkez.notificationservice.dto.event.UserRegisteredEventDto;
import com.avmerkez.notificationservice.dto.event.CampaignCreatedEventDto;
import com.avmerkez.notificationservice.dto.event.EventCreatedEventDto;
import com.avmerkez.notificationservice.dto.event.ReviewCreatedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerListener {

    @KafkaListener(topics = "${avmerkez.kafka.topics.user-registration}", 
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "kafkaListenerContainerFactory") // Default factory name
    public void handleUserRegistrationEvent(@Payload UserRegisteredEventDto event) {
        log.info("Received UserRegisteredEvent from Kafka: User ID = {}, Email = {}", event.getUserId(), event.getEmail());

        try {
            // TODO: Implement actual notification sending logic based on the event
            // Example: Send a welcome email
            // String subject = "Welcome to AVMerkez!";
            // String body = String.format("Hi %s, thanks for registering!", event.getFirstName());
            // emailService.sendEmail(event.getEmail(), subject, body);
            log.info("Processing notification for registered user: {}", event.getUsername());

        } catch (Exception e) {
            log.error("Failed to process user registration notification for user ID {}: {}", event.getUserId(), e.getMessage(), e);
            // Consider retry mechanisms or dead-letter queue for failed processing
        }
    }

    @KafkaListener(topics = "${avmerkez.kafka.topics.campaign-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleCampaignCreatedEvent(@Payload CampaignCreatedEventDto event) {
        log.info("Received CampaignCreatedEvent from Kafka: Campaign ID = {}, Name = {}", event.getCampaignId(), event.getName());
        // TODO: Kampanya bildirimi işlemleri (ör: push notification)
    }

    @KafkaListener(topics = "${avmerkez.kafka.topics.event-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleEventCreatedEvent(@Payload EventCreatedEventDto event) {
        log.info("Received EventCreatedEvent from Kafka: Event ID = {}, Name = {}", event.getEventId(), event.getName());
        // TODO: Etkinlik bildirimi işlemleri (ör: push notification)
    }

    @KafkaListener(topics = "${avmerkez.kafka.topics.review-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleReviewCreatedEvent(@Payload ReviewCreatedEventDto event) {
        log.info("Received ReviewCreatedEvent from Kafka: Review ID = {}, User ID = {}", event.getReviewId(), event.getUserId());
        // TODO: Yorum bildirimi işlemleri
    }

    // Add other listeners for different topics/events here
} 