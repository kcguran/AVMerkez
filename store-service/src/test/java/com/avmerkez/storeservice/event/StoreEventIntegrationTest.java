package com.avmerkez.storeservice.event;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"store-created"})
@ActiveProfiles("test")
class StoreEventIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testStoreCreatedEvent() throws Exception {
        StoreCreatedEventDto event = StoreCreatedEventDto.builder()
            .storeId(1L)
            .name("Test Store")
            .build();
        kafkaTemplate.send(new ProducerRecord<>("store-created", event.getStoreId().toString(), event));
        Thread.sleep(2000);
        // Assert veya log kontrolü eklenebilir
    }
} 