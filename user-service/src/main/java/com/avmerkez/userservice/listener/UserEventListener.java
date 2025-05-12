package com.avmerkez.userservice.listener;

import com.avmerkez.userservice.dto.event.UserRegisteredEventDto;
import com.avmerkez.userservice.entity.User;
import com.avmerkez.userservice.event.UserRegisteredEvent;
import com.avmerkez.userservice.notification.NotificationContext;
import com.avmerkez.userservice.notification.NotificationService;
import com.avmerkez.userservice.notification.NotificationType;
import com.avmerkez.userservice.notification.TargetPlatform;
import com.avmerkez.userservice.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);
    private final NotificationService notificationService;
    private final KafkaProducerService kafkaProducerService;

    @Async
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.getUser();
        logger.info("User registered event received for user: {} (ID: {}).", 
                    user.getUsername(), user.getId());

        try {
            UserRegisteredEventDto kafkaPayload = UserRegisteredEventDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .registeredAt(user.getCreatedAt())
                .build();
            kafkaProducerService.sendUserRegistrationEvent(kafkaPayload);
        } catch (Exception e) {
            logger.error("Failed to send UserRegisteredEvent to Kafka for user {}: {}", user.getId(), e.getMessage(), e);
        }

        sendWelcomeNotifications(user);
    }

    private void sendWelcomeNotifications(User user) {
        NotificationContext emailContext = NotificationContext.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .emailAddress(user.getEmail())
                .notificationType(NotificationType.USER_REGISTRATION_WELCOME)
                .targetPlatform(TargetPlatform.EMAIL)
                .title("AVMerkez'e Hoş Geldiniz!")
                .messageBody(String.format("Merhaba %s, AVMerkez platformuna başarıyla kaydoldunuz.", user.getFirstName()))
                .build();
        
        try {
            notificationService.sendNotification(emailContext);
        } catch (Exception e) {
            logger.error("Failed to send welcome email notification for user {}: {}", user.getId(), e.getMessage(), e);
        }

        NotificationContext webPushContext = NotificationContext.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .notificationType(NotificationType.USER_REGISTRATION_WELCOME)
                .targetPlatform(TargetPlatform.WEB_PUSH)
                .title("Hoş Geldiniz!")
                .messageBody(String.format("%s, AVMerkez'e hoş geldiniz! Size özel fırsatları keşfedin.", user.getFirstName()))
                .build();
        
        try {
            notificationService.sendNotification(webPushContext);
        } catch (Exception e) {
            logger.error("Failed to send welcome web_push notification for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }
} 