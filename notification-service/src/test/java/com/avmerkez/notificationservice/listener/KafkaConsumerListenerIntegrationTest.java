package com.avmerkez.notificationservice.listener;

import com.avmerkez.notificationservice.dto.event.CampaignCreatedEventDto;
import com.avmerkez.notificationservice.dto.event.EventCreatedEventDto;
import com.avmerkez.notificationservice.dto.event.ReviewCreatedEventDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"campaign-created", "event-created", "review-created"})
@ActiveProfiles("test")
class KafkaConsumerListenerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testCampaignCreatedEventConsumed() throws Exception {
        CampaignCreatedEventDto event = CampaignCreatedEventDto.builder()
                .campaignId(1L)
                .name("Test Kampanya")
                .description("Test açıklama")
                .mallId(1L)
                .storeId(2L)
                .brandId(3L)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        kafkaTemplate.send(new ProducerRecord<>("campaign-created", event.getCampaignId().toString(), event));
        Thread.sleep(2000); // Listener'ın çalışması için kısa bekleme
        // Gerçek assert için log veya mock ile kontrol eklenebilir
    }

    @Test
    void testEventCreatedEventConsumed() throws Exception {
        EventCreatedEventDto event = EventCreatedEventDto.builder()
                .eventId(2L)
                .name("Test Etkinlik")
                .description("Etkinlik açıklaması")
                .mallId(1L)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .locationDescription("Zemin Kat")
                .category("Konser")
                .build();
        kafkaTemplate.send(new ProducerRecord<>("event-created", event.getEventId().toString(), event));
        Thread.sleep(2000);
    }

    @Test
    void testReviewCreatedEventConsumed() throws Exception {
        ReviewCreatedEventDto event = ReviewCreatedEventDto.builder()
                .reviewId(3L)
                .userId(1L)
                .mallId(1L)
                .storeId(2L)
                .rating(5)
                .comment("Çok iyi!")
                .createdAt(LocalDateTime.now())
                .build();
        kafkaTemplate.send(new ProducerRecord<>("review-created", event.getReviewId().toString(), event));
        Thread.sleep(2000);
    }
} 