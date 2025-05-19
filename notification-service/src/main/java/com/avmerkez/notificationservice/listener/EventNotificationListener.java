package com.avmerkez.notificationservice.listener;

import com.avmerkez.notificationservice.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventNotificationListener {
    private static final Logger logger = LoggerFactory.getLogger(EventNotificationListener.class);
    private final PushNotificationService pushNotificationService;

    @KafkaListener(topics = {"campaign-events", "event-events", "review-events"}, groupId = "notification-service")
    public void handleEvent(String eventJson) {
        logger.info("[KAFKA] Yeni event alındı: {}", eventJson);
        // Dummy: Her eventte push notification tetikle
        pushNotificationService.sendPushNotification("dummyUserId", "Yeni Etkinlik", "Bir etkinlik/kampanya/yorum oluştu!");
    }
} 