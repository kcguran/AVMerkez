package com.avmerkez.userservice.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final List<NotificationStrategy> strategies;

    // Spring, @Component olarak işaretlenmiş tüm NotificationStrategy beanlerini bu listeye inject edecek.
    public NotificationServiceImpl(List<NotificationStrategy> strategies) {
        this.strategies = strategies;
        logger.info("NotificationServiceImpl initialized with {} strategies.", strategies.size());
        strategies.forEach(s -> logger.info("  Loaded strategy: {}", s.getClass().getSimpleName()));
    }

    @Override
    public void sendNotification(NotificationContext context) {
        boolean sent = false;
        for (NotificationStrategy strategy : strategies) {
            if (strategy.supports(context.getNotificationType(), context.getTargetPlatform())) {
                try {
                    logger.debug("Attempting to send notification via strategy: {} for type: {} and platform: {}", 
                                 strategy.getClass().getSimpleName(), context.getNotificationType(), context.getTargetPlatform());
                    strategy.send(context);
                    sent = true;
                    logger.info("Notification sent successfully via {} for user ID: {}", 
                                strategy.getClass().getSimpleName(), context.getUserId());
                    // Genellikle bir bildirim tipi ve platformu için tek bir strateji çalışır.
                    // Eğer birden fazla stratejinin aynı anda çalışması isteniyorsa (örn: hem logla hem e-posta gönder)
                    // buradaki break kaldırılabilir veya supports() mantığı değiştirilebilir.
                    // Şimdilik ilk uygun strateji gönderimi yapar ve durur.
                    break; 
                } catch (NotificationException e) {
                    logger.error("Notification sending failed via strategy {} for user ID {}: {}", 
                                 strategy.getClass().getSimpleName(), context.getUserId(), e.getMessage(), e);
                    // Bir strateji başarısız olursa diğerini denemeye devam edebilir veya burada durabilir.
                    // Şimdilik, bir hata durumunda loglayıp diğer stratejileri denemez (break nedeniyle).
                } catch (Exception e) {
                    logger.error("Unexpected error during notification sending via strategy {} for user ID {}: {}", 
                                 strategy.getClass().getSimpleName(), context.getUserId(), e.getMessage(), e);
                }
            }
        }
        if (!sent) {
            logger.warn("No suitable notification strategy found for type: {} and platform: {} for user ID: {}",
                    context.getNotificationType(), context.getTargetPlatform(), context.getUserId());
        }
    }
} 