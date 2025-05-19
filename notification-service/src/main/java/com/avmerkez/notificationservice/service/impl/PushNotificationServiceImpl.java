package com.avmerkez.notificationservice.service.impl;

import com.avmerkez.notificationservice.service.PushNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
    @Override
    public void sendPushNotification(String userId, String title, String message) {
        logger.info("[PUSH] userId={}, title={}, message={}", userId, title, message);
    }
} 