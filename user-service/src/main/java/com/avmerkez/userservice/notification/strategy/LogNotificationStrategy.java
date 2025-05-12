package com.avmerkez.userservice.notification.strategy;

import com.avmerkez.userservice.notification.NotificationContext;
import com.avmerkez.userservice.notification.NotificationStrategy;
import com.avmerkez.userservice.notification.NotificationType;
import com.avmerkez.userservice.notification.TargetPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component // Spring tarafından bulunabilmesi için
public class LogNotificationStrategy implements NotificationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(LogNotificationStrategy.class);

    @Override
    public void send(NotificationContext context) {
        logger.info("Simulating notification send via LOGGING strategy:");
        logger.info("  To User ID: {}", context.getUserId());
        logger.info("  Username: {}", context.getUsername());
        logger.info("  Email: {}", context.getEmailAddress());
        logger.info("  Type: {}", context.getNotificationType());
        logger.info("  Platform: {}", context.getTargetPlatform());
        logger.info("  Title: {}", context.getTitle());
        logger.info("  Message: {}", context.getMessageBody());
        if (context.getDataPayload() != null && !context.getDataPayload().isEmpty()) {
            logger.info("  Data Payload: {}", context.getDataPayload());
        }
    }

    @Override
    public boolean supports(NotificationType notificationType, TargetPlatform targetPlatform) {
        // Bu strateji, test amacıyla tüm e-posta ve web push bildirimlerini loglar.
        return targetPlatform == TargetPlatform.EMAIL || targetPlatform == TargetPlatform.WEB_PUSH;
    }
} 