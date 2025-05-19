# AVMerkez API Entegrasyon Dokümantasyonu

Bu doküman, frontend ve mobil uygulama ekiplerinin AVMerkez backend API'sine entegre olurken ihtiyaç duyacağı temel endpoint listesini, örnek request/response'ları ve entegrasyon için gerekli bilgileri özetler.

---

## Authentication & Temel Bilgiler
- **JWT zorunlu:** Tüm endpointlerde JWT gereklidir (public endpointler hariç).
- **JWT iletim şekli:**
  - Web: HttpOnly cookie (tarayıcıda otomatik)
  - Mobil: Authorization: Bearer ... header'ı ile
- **CORS:** API Gateway'de merkezi olarak tanımlı, frontend/mobil domaininizi backend ekibiyle paylaşın.
- **Rate Limit:** Varsayılan 1000 istek/saat/kullanıcı (değişebilir).
- **Hata Yönetimi:** Standart HTTP kodları ve açıklamalı JSON hata yanıtı.

---

## Kullanıcı Servisi (User Service)

### Kayıt
`POST /api/v1/auth/register`
```json
{
  "username": "keremguran",
  "email": "kerem@example.com",
  "password": "Password123!",
  "firstName": "Kerem",
  "lastName": "Güran"
}
```
Yanıt:
```json
{
  "success": true,
  "message": "Kullanıcı başarıyla kaydedildi."
}
```

### Login
`POST /api/v1/auth/login`
```json
{
  "username": "keremguran",
  "password": "Password123!"
}
```
Yanıt:
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

### Profil
`GET /api/v1/users/me`
Yanıt:
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
`POST /api/v1/users/change-password`
```json
{
  "oldPassword": "Password123!",
  "newPassword": "NewPassword456!"
}
```
Yanıt:
```json
{
  "success": true,
  "message": "Şifre başarıyla değiştirildi."
}
```

### Favori AVM/Mağaza Ekle
`POST /api/v1/users/favorites/malls/{mallId}`
Yanıt:
```json
{
  "success": true,
  "message": "Favori AVM eklendi."
}
```

### Favori AVM/Mağaza Listele
`GET /api/v1/users/favorites/malls`
Yanıt:
```json
{
  "success": true,
  "data": [12, 15]
}
```

### Favori AVM/Mağaza Sil
`DELETE /api/v1/users/favorites/malls/{mallId}`
Yanıt:
```json
{
  "success": true,
  "message": "Favori AVM silindi."
}
```

### JWT Refresh
`POST /api/v1/auth/refresh-token`
Yanıt:
```json
{
  "success": true,
  "message": "Yeni JWT üretildi."
}
```

### Logout
`POST /api/v1/auth/logout`
Yanıt:
```json
{
  "success": true,
  "message": "Çıkış yapıldı."
}
```

---

## AVM Servisi (Mall Service)

### Tüm AVM'ler
`GET /api/v1/malls`
Yanıt:
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
`GET /api/v1/malls/{mallId}`
Yanıt:
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
`GET /api/v1/malls/near?lat=39.87&lon=32.85&distKm=5`
Yanıt:
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
`GET /api/v1/malls?city=Ankara&district=Çankaya`
Yanıt:
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

---

## Mağaza Servisi (Store Service)

### Tüm Mağazalar
`GET /api/v1/stores`
Yanıt:
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
`GET /api/v1/malls/{mallId}/stores`
Yanıt:
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
`GET /api/v1/categories/{categoryId}/stores`
Yanıt:
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
`GET /api/v1/brands/{brandId}/stores`
Yanıt:
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
`GET /api/v1/stores/{storeId}`
Yanıt:
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

---

## Yorum Servisi (Review Service)

### Yorum Ekle
`POST /api/v1/reviews`
```json
{
  "mallId": 12,
  "rating": 5,
  "comment": "Çok temiz ve ferah bir AVM."
}
```
Yanıt:
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
`GET /api/v1/reviews/public/mall/{mallId}`
Yanıt:
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
`GET /api/v1/reviews/public/store/{storeId}`
Yanıt:
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
`GET /api/v1/reviews/user`
Yanıt:
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

---

## Kampanya/Etkinlik Servisi

### Kampanyalar
`GET /api/v1/campaigns`
Yanıt:
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
`GET /api/v1/events`
Yanıt:
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
`GET /api/v1/events?mallId=12&dateFrom=2024-06-01&dateTo=2024-06-30`
Yanıt:
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

---

## Arama Servisi (Search Service)

### Serbest Metin Arama
`POST /api/v1/search`
```json
{
  "query": "teknosa",
  "onlyFavorites": true
}
```
Yanıt:
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

---

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

---

## Entegrasyon Notları
- Tüm endpointlerde JWT zorunlu (public endpointler hariç)
- CORS ve rate limit için domaininizi backend ekibiyle paylaşın
- Refresh token akışını uygulayın (mobilde header, webde cookie)
- API endpointlerini Swagger UI üzerinden test edebilirsiniz (örn: http://localhost:8080/swagger-ui.html)

---

## İletişim
- Ortam değişkenleri, test kullanıcıları ve özel ihtiyaçlar için backend ekibiyle iletişime geçin 