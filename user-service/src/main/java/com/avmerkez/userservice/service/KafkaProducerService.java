package com.avmerkez.userservice.service; // Or com.avmerkez.userservice.kafka

import com.avmerkez.userservice.dto.event.UserRegisteredEventDto; // Assuming this DTO will be created
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${avmerkez.kafka.topics.user-registration}")
    private String userRegistrationTopic;

    public void sendUserRegistrationEvent(UserRegisteredEventDto eventPayload) {
        try {
            log.info("Sending user registration event to topic '{}': {}", userRegistrationTopic, eventPayload);
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(userRegistrationTopic, eventPayload.getUserId().toString(), eventPayload);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent message=[{}] with offset=[{}] to topic={}", eventPayload, result.getRecordMetadata().offset(), userRegistrationTopic);
                } else {
                    log.error("Unable to send message=[{}] due to : {}", eventPayload, ex.getMessage(), ex);
                    // Consider adding to a dead-letter queue or retry mechanism here
                }
            });
        } catch (Exception e) {
            log.error("Failed to send user registration event to Kafka topic {} due to an unexpected error", userRegistrationTopic, e);
        }
    }
} 