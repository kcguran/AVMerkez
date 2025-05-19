package com.avmerkez.mallservice.service.impl;

import com.avmerkez.mallservice.event.MallCreatedEvent;
import com.avmerkez.mallservice.service.KafkaProducerService;
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
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, MallCreatedEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.mall-created:mall-created}")
    private String mallCreatedTopic;

    @Override
    public void sendMallCreatedEvent(MallCreatedEvent event) {
        try {
            CompletableFuture<SendResult<String, MallCreatedEvent>> future = kafkaTemplate.send(mallCreatedTopic, String.valueOf(event.getId()), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Mall created event sent successfully: id={}, offset={}, partition={}", 
                            event.getId(), result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
                } else {
                    log.error("Failed to send mall created event: id={}", event.getId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error occurred while sending mall created event: id={}", event.getId(), e);
        }
    }
} 