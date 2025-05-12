package com.avmerkez.userservice.notification;

public enum TargetPlatform {
    EMAIL,
    WEB_PUSH,       // Tarayıcı anlık bildirimleri
    MOBILE_PUSH     // Genel mobil anlık bildirim (FCM, APNS vb. için alt tipler olabilir)
} 