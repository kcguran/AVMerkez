# AVMerkez Backend API & Entegrasyon Dokümantasyonu

## Genel Mimari
- **Backend:** Java 17+, Spring Boot 3.x, mikroservis mimarisi
- **Servisler:**
  - API Gateway (tek giriş noktası, JWT doğrulama, rate limit, CORS)
  - User Service (kayıt, login, profil, favoriler, roller, şifre)
  - Mall Service (AVM CRUD, konum bazlı arama, detay)
  - Store Service (mağaza CRUD, kategori, marka, AVM içi mağazalar)
  - Review Service (yorum ekle/listele, puanlama, onay)
  - Campaign/Event Service (kampanya, etkinlik, tarih validasyonu)
  - Notification Service (push notification, event dinleme)
  - Search Service (Elasticsearch, facet, öneri, favori ile arama)
- **İletişim:** RESTful JSON API, JWT tabanlı authentication, servisler arası Feign/Kafka

## Authentication & Güvenlik
- **JWT:**
  - Login sonrası JWT, HttpOnly cookie olarak döner (frontend: cookie ile otomatik gönderim, mobil: header ile gönderim önerilir)
  - Refresh token desteği (cookie)
  - Tüm endpointler JWT gerektirir, public endpointler hariç
- **CORS:**
  - API Gateway üzerinden merkezi CORS ayarı (frontend/mobil domainleri eklenmeli)
- **Rate Limiting:**
  - Gateway'de Redis tabanlı rate limit (ör: 1000 req/saat/kullanıcı)
- **Hata Yönetimi:**
  - Standart HTTP kodları, açıklamalı JSON hata yanıtı
  - Validation hataları: 400, kimlik: 401, yetki: 403, bulunamadı: 404, sunucu: 500

## Kullanıcı Servisi (User Service)

### Kayıt
**POST /api/v1/auth/register**

**Request:**
```json
{
  "username": "keremguran",
  "email": "kerem@example.com",
  "password": "Password123!",
  "firstName": "Kerem",
  "lastName": "Güran"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Kullanıcı başarıyla kaydedildi."
}
```

### Login
**POST /api/v1/auth/login**

**Request:**
```json
{
  "username": "keremguran",
  "password": "Password123!"
}
```
**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "keremguran",
    "email": "kerem@example.com",
    "roles": ["USER"]
  }
}
```
> JWT, HttpOnly cookie olarak döner. Mobilde ayrıca response header veya body'den alınabilir.

### Profil
**GET /api/v1/users/me**

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "keremguran",
    "email": "kerem@example.com",
    "firstName": "Kerem",
    "lastName": "Güran",
    "roles": ["USER"],
    "favoriteMalls": [12, 15],
    "favoriteStores": [101, 202]
  }
}
```

### Şifre Değiştir
**POST /api/v1/users/change-password**

**Request:**
```json
{
  "oldPassword": "Password123!",
  "newPassword": "NewPassword456!"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Şifre başarıyla değiştirildi."
}
```

### Favori AVM/Mağaza Ekle
**POST /api/v1/users/favorites/malls/{mallId}**

**Response:**
```json
{
  "success": true,
  "message": "Favori AVM eklendi."
}
```

### Favori AVM/Mağaza Listele
**GET /api/v1/users/favorites/malls**

**Response:**
```json
{
  "success": true,
  "data": [12, 15]
}
```

### Favori AVM/Mağaza Sil
**DELETE /api/v1/users/favorites/malls/{mallId}**

**Response:**
```json
{
  "success": true,
  "message": "Favori AVM silindi."
}
```

### JWT Refresh
**POST /api/v1/auth/refresh-token**

**Response:**
```json
{
  "success": true,
  "message": "Yeni JWT üretildi."
}
```

### Logout
**POST /api/v1/auth/logout**

**Response:**
```json
{
  "success": true,
  "message": "Çıkış yapıldı."
}
```

## AVM Servisi (Mall Service)

### Tüm AVM'ler
**GET /api/v1/malls**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 12,
      "name": "Ankara Panora AVM",
      "city": "Ankara",
      "district": "Çankaya",
      "address": "Turan Güneş Bulvarı No:182",
      "latitude": 39.87,
      "longitude": 32.85,
      "workingHours": "10:00-22:00",
      "website": "https://panora.com.tr",
      "phoneNumber": "+90 312 491 2525"
    }
  ]
}
```

### Detay
**GET /api/v1/malls/{mallId}**

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 12,
    "name": "Ankara Panora AVM",
    "city": "Ankara",
    "district": "Çankaya",
    "address": "Turan Güneş Bulvarı No:182",
    "latitude": 39.87,
    "longitude": 32.85,
    "workingHours": "10:00-22:00",
    "website": "https://panora.com.tr",
    "phoneNumber": "+90 312 491 2525"
  }
}
```

### Konum Bazlı Arama
**GET /api/v1/malls/near?lat=39.87&lon=32.85&distKm=5**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 12,
      "name": "Ankara Panora AVM",
      "latitude": 39.87,
      "longitude": 32.85
    }
  ]
}
```

### Filtre
**GET /api/v1/malls?city=Ankara&district=Çankaya**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 12,
      "name": "Ankara Panora AVM",
      "city": "Ankara",
      "district": "Çankaya"
    }
  ]
}
```

## Mağaza Servisi (Store Service)

### Tüm Mağazalar
**GET /api/v1/stores**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 101,
      "name": "Teknosa",
      "mallId": 12,
      "categoryId": 5,
      "brandId": 3,
      "floor": "Zemin",
      "storeNo": "A12"
    }
  ]
}
```

### AVM'deki Mağazalar
**GET /api/v1/malls/{mallId}/stores**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 101,
      "name": "Teknosa",
      "mallId": 12
    }
  ]
}
```

### Kategoriye Göre
**GET /api/v1/categories/{categoryId}/stores**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 101,
      "name": "Teknosa",
      "categoryId": 5
    }
  ]
}
```

### Markaya Göre
**GET /api/v1/brands/{brandId}/stores**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 101,
      "name": "Teknosa",
      "brandId": 3
    }
  ]
}
```

### Detay
**GET /api/v1/stores/{storeId}**

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 101,
    "name": "Teknosa",
    "mallId": 12,
    "categoryId": 5,
    "brandId": 3,
    "floor": "Zemin",
    "storeNo": "A12"
  }
}
```

## Yorum Servisi (Review Service)

### Yorum Ekle
**POST /api/v1/reviews**

**Request:**
```json
{
  "mallId": 12,
  "rating": 5,
  "comment": "Çok temiz ve ferah bir AVM."
}
```
**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1001,
    "userId": 1,
    "mallId": 12,
    "rating": 5,
    "comment": "Çok temiz ve ferah bir AVM.",
    "createdAt": "2024-06-01T12:34:56Z",
    "approvalStatus": "PENDING"
  }
}
```

### AVM Yorumları
**GET /api/v1/reviews/public/mall/{mallId}**

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1001,
        "userId": 1,
        "mallId": 12,
        "rating": 5,
        "comment": "Çok temiz ve ferah bir AVM.",
        "createdAt": "2024-06-01T12:34:56Z",
        "approvalStatus": "APPROVED"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
}
```

### Mağaza Yorumları
**GET /api/v1/reviews/public/store/{storeId}**

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1002,
        "userId": 2,
        "storeId": 101,
        "rating": 4,
        "comment": "Çalışanlar ilgili.",
        "createdAt": "2024-06-01T13:00:00Z",
        "approvalStatus": "APPROVED"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
}
```

### Kullanıcı Yorumları
**GET /api/v1/reviews/user**

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1001,
        "mallId": 12,
        "rating": 5,
        "comment": "Çok temiz ve ferah bir AVM.",
        "createdAt": "2024-06-01T12:34:56Z",
        "approvalStatus": "APPROVED"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
}
```

## Kampanya/Etkinlik Servisi

### Kampanyalar
**GET /api/v1/campaigns**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 2001,
      "name": "Yaz İndirimi",
      "description": "Tüm ürünlerde %20 indirim!",
      "startDate": "2024-06-01",
      "endDate": "2024-06-15"
    }
  ]
}
```

### Etkinlikler
**GET /api/v1/events**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 3001,
      "name": "Çocuk Tiyatrosu",
      "mallId": 12,
      "startDate": "2024-06-10",
      "endDate": "2024-06-10",
      "description": "Çocuklar için ücretsiz tiyatro etkinliği."
    }
  ]
}
```

### Etkinlik Filtreleme
**GET /api/v1/events?mallId=12&dateFrom=2024-06-01&dateTo=2024-06-30**

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 3001,
      "name": "Çocuk Tiyatrosu",
      "mallId": 12,
      "startDate": "2024-06-10",
      "endDate": "2024-06-10"
    }
  ]
}
```

## Arama Servisi (Search Service)

### Serbest Metin Arama
**POST /api/v1/search**

**Request:**
```json
{
  "query": "teknosa",
  "onlyFavorites": true,
  "facet": true
}
```
**Response:**
```json
{
  "success": true,
  "data": {
    "results": [
      {
        "id": 101,
        "name": "Teknosa",
        "type": "store",
        "mallId": 12
      }
    ],
    "cityCounts": {"Ankara": 5, "İstanbul": 3},
    "categoryCounts": {"Elektronik": 2, "Giyim": 1},
    "suggestions": ["Teknosa Outlet", "TeknoMarket"]
  }
}
```

## Bildirim Servisi
- Push notification dummy: Otomatik event ile tetiklenir, frontend/mobil için WebSocket veya FCM entegrasyonu ileride eklenebilir

## JWT Kullanımı
- **Frontend:** Cookie tabanlı JWT otomatik gönderilir (tarayıcıda)
- **Mobil:** JWT, login yanıtından alınır, Authorization: Bearer ... header'ı ile gönderilir

## Hata Yanıtı Örneği
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "field": "error message"
  }
}
```
## Entegrasyon Akışları
- Kayıt/Login → JWT alınır → Tüm isteklerde JWT otomatik gönderilir
- Favori AVM/Mağaza → Favori ekle/sil/listele endpointleri
- Yorum → JWT ile yorum ekle, kendi yorumlarını listele
- Arama → POST ile sorgu gönder, facet ve öneri al

## Event/Mesajlaşma Entegrasyonu

### Temel Event Şemaları ve Örnek Payload'lar

#### MallCreatedEvent
```json
{
  "eventType": "MallCreatedEvent",
  "mallId": 12,
  "name": "Ankara Panora AVM",
  "city": "Ankara",
  "district": "Çankaya",
  "latitude": 39.87,
  "longitude": 32.85,
  "createdAt": "2024-06-01T12:00:00Z"
}
```

#### MallDeletedEvent
```json
{
  "eventType": "MallDeletedEvent",
  "mallId": 12,
  "deletedAt": "2024-06-01T13:00:00Z"
}
```

#### StoreCreatedEvent
```json
{
  "eventType": "StoreCreatedEvent",
  "storeId": 101,
  "mallId": 12,
  "name": "Teknosa",
  "categoryId": 5,
  "brandId": 3,
  "createdAt": "2024-06-01T14:00:00Z"
}
```

#### CampaignCreatedEvent
```json
{
  "eventType": "CampaignCreatedEvent",
  "campaignId": 2001,
  "name": "Yaz İndirimi",
  "startDate": "2024-06-01",
  "endDate": "2024-06-15",
  "mallId": 12,
  "createdAt": "2024-06-01T15:00:00Z"
}
```

#### EventCreatedEvent
```json
{
  "eventType": "EventCreatedEvent",
  "eventId": 3001,
  "name": "Çocuk Tiyatrosu",
  "mallId": 12,
  "startDate": "2024-06-10",
  "endDate": "2024-06-10",
  "createdAt": "2024-06-01T16:00:00Z"
}
```

#### ReviewCreatedEvent
```json
{
  "eventType": "ReviewCreatedEvent",
  "reviewId": 1001,
  "userId": 1,
  "mallId": 12,
  "storeId": null,
  "rating": 5,
  "comment": "Çok temiz ve ferah bir AVM.",
  "createdAt": "2024-06-01T12:34:56Z"
}
```

### Servislerin Event Yayınlama ve Dinleme Akışları

#### 1. mall-service
- **Yayınlar:**
  - MallCreatedEvent
  - MallDeletedEvent
- **Dinler:**
  - (Yok, sadece yayıncı)

#### 2. store-service
- **Yayınlar:**
  - StoreCreatedEvent
- **Dinler:**
  - MallDeletedEvent (Bir AVM silinirse ilgili mağazaları güncelleyebilir veya soft-delete yapabilir)

#### 3. campaign-event-service
- **Yayınlar:**
  - CampaignCreatedEvent
  - EventCreatedEvent
- **Dinler:**
  - MallDeletedEvent (Bir AVM silinirse ilgili kampanya/etkinlikleri güncelleyebilir)

#### 4. review-service
- **Yayınlar:**
  - ReviewCreatedEvent
- **Dinler:**
  - MallDeletedEvent, StoreCreatedEvent (gerekirse)

#### 5. search-service
- **Yayınlar:**
  - (Yok, sadece dinleyici)
- **Dinler:**
  - MallCreatedEvent, MallDeletedEvent, StoreCreatedEvent, CampaignCreatedEvent, EventCreatedEvent, ReviewCreatedEvent
  - (Tüm bu event'lerle kendi Elasticsearch indeksini günceller)

#### 6. notification-service
- **Yayınlar:**
  - (Yok, sadece dinleyici)
- **Dinler:**
  - CampaignCreatedEvent, EventCreatedEvent, ReviewCreatedEvent (Kullanıcıya push notification tetikler)

#### 7. user-service
- **Yayınlar:**
  - UserRegisteredEvent (Kafka ile veya sadece internal event olabilir)
- **Dinler:**
  - (Yok)

### Akış Diyagramı (Metin)

```
mall-service --(MallCreatedEvent)--> search-service
mall-service --(MallDeletedEvent)--> store-service, campaign-event-service, search-service
store-service --(StoreCreatedEvent)--> search-service
campaign-event-service --(CampaignCreatedEvent/EventCreatedEvent)--> search-service, notification-service
review-service --(ReviewCreatedEvent)--> search-service, notification-service
user-service --(UserRegisteredEvent)--> notification-service (opsiyonel)
```

- **search-service**: Tüm event'leri dinler, kendi arama indeksini günceller.
- **notification-service**: Kampanya, etkinlik, yorum event'lerini dinler, push notification tetikler.