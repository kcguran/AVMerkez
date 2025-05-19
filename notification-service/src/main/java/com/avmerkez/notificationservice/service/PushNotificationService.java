package com.avmerkez.notificationservice.service;

public interface PushNotificationService {
    void sendPushNotification(String userId, String title, String message);
} 