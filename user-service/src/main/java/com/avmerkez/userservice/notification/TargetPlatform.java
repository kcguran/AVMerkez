package com.avmerkez.userservice.notification;

public enum TargetPlatform {
    EMAIL,
    WEB_PUSH,       // Tarayıcı anlık bildirimleri
    MOBILE_PUSH     // Mobil anlık bildirim (ör. FCM, APNS)
} 