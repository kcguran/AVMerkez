package com.avmerkez.userservice.notification;

public interface NotificationStrategy {
    void send(NotificationContext context) throws NotificationException;
    boolean supports(NotificationType notificationType, TargetPlatform targetPlatform);
} 