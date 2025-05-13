package com.avmerkez.notificationservice.listener;

import com.avmerkez.notificationservice.dto.event.UserRegisteredEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerListener {

    // Inject necessary services like EmailService, PushNotificationService etc.
    // private final EmailService emailService;

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

    // Add other listeners for different topics/events here
} 