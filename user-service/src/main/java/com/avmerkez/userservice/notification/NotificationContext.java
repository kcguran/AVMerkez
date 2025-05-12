package com.avmerkez.userservice.notification;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class NotificationContext {
    private String userId; // Hedef kullanıcı ID'si
    private String emailAddress; // E-posta için
    private String username;
    private NotificationType notificationType;
    private TargetPlatform targetPlatform;
    
    // Stratejiye özel başlık, mesaj veya şablon ID'si
    private String title; 
    private String messageBody; // veya messageTemplateId
    
    private Map<String, Object> dataPayload; // Platforma veya şablona özel ek veriler (örn: deep link, resim URL'si)
} 