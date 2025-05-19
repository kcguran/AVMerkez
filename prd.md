# AVMerkez Projesi - Ürün Gereksinim Dokümanı (PRD)

**Versiyon:** 0.4
**Tarih:** {CurrentDate}

## 1. Proje Adı ve Tanımı

**Proje Adı:** AVMerkez
**Tanım:** Türkiye için Alışveriş Merkezi Rehber Uygulaması. Kullanıcıların Türkiye'deki AVM'leri, içindeki mağazaları, etkinlikleri, kampanyaları, kat planlarını ve diğer AVM hizmetlerini keşfetmelerini sağlayan bir platformdur. Backend, Java Spring Boot ve mikroservis mimarisi kullanılarak geliştirilecektir.

## 2. Hedefler

*   Kullanıcılara Türkiye'deki AVM'ler hakkında kapsamlı ve güncel bilgi sunmak.
*   Mağaza arama, kampanya takibi ve etkinlik keşfetme süreçlerini kolaylaştırmak.
*   AVM ve mağazalar için kullanıcı yorumları ve puanlamaları platformu oluşturmak.
*   Konum tabanlı servislerle kullanıcılara en yakın AVM'leri ve fırsatları göstermek.
*   Genişletilebilir ve bakımı kolay bir backend altyapısı kurmak.

## 3. Hedef Kitle

*   Alışveriş yapmayı seven ve AVM'leri sık ziyaret eden son kullanıcılar.
*   Belirli bir mağazayı, markayı veya ürünü arayan tüketiciler.
*   AVM'lerdeki etkinlik ve kampanyalardan haberdar olmak isteyen kişiler.
*   Şehir dışından gelen ve ziyaret edeceği AVM hakkında bilgi almak isteyen turistler/ziyaretçiler.

## 4. Temel Özellikler ve Kullanıcı Hikayeleri

Bu bölümde, temel özellikler mikroservis bazında gruplandırılmış ve bazı örnek kullanıcı hikayeleri ile detaylandırılmıştır.

*   **AVM Servisi (Mall Service):** AVM ile ilgili temel bilgileri yönetir.
    *   **Özellikler:**
        *   AVM listeleme (filtreleme: şehir, ilçe; sıralama: ad, popülerlik, yakınlık).
        *   AVM detaylarını görüntüleme (ad, adres, çalışma saatleri, hizmetler, iletişim bilgileri, konum (lat/lon), kat planı URL'leri).
        *   Konum bazlı arama (belirli bir koordinata yakın AVM'leri bulma).
    *   **Örnek Kullanıcı Hikayeleri:**
        *   *Bir kullanıcı olarak, İstanbul'daki AVM'leri listelemek istiyorum, böylece ziyaret edebileceğim yerleri görebilirim.*
        *   *Bir kullanıcı olarak, seçtiğim bir AVM'nin çalışma saatlerini ve otopark hizmeti olup olmadığını görmek istiyorum, böylece ziyaretimi planlayabilirim.*
        *   *Bir kullanıcı olarak, mevcut konumuma en yakın 5 AVM'yi haritada görmek istiyorum.*

*   **Mağaza Servisi (Store Service):** AVM'lerdeki mağazaları ve ilgili bilgileri yönetir. Kategori ve marka bilgileri de bu servis altında yönetilir.
    *   **Özellikler:**
        *   Bir AVM içindeki mağazaları listeleme (filtreleme: kategori, kat; arama: mağaza adı).
        *   Mağaza detaylarını görüntüleme (ad, kat, mağaza no, kategori, marka, iletişim, logo).
        *   Marka veya kategoriye göre farklı AVM'derdeki mağazaları arama.
        *   Kategori yönetimi (CRUD - Admin).
        *   Marka yönetimi (CRUD - Admin).
        *   Kategori ve marka listeleme (Kullanıcılar için).
    *   **Örnek Kullanıcı Hikayeleri:**
        *   *Bir kullanıcı olarak, X AVM'sindeki elektronik mağazalarını listelemek istiyorum.*
        *   *Bir kullanıcı olarak, Y markasının hangi AVM'lerde mağazası olduğunu bulmak istiyorum.*

*   **Kullanıcı Servisi (User Service):** Kullanıcı kimlik doğrulama, yetkilendirme ve profil yönetimini sağlar.
    *   **Özellikler:**
        *   E-posta/şifre ile kullanıcı kaydı.
        *   JWT (JSON Web Token) tabanlı kullanıcı girişi ve oturum yönetimi.
        *   Rol tabanlı yetkilendirme (USER, ADMIN, AVM_YONETICISI).
        *   Kullanıcı profili görüntüleme ve güncelleme (ad, soyad vb.).
        *   Kullanıcı şifre güncelleme (mevcut şifre kontrolü ile).
        *   Kullanıcı rol yönetimi (admin tarafından rollerin güncellenmesi).
        *   Favori AVM/Mağaza listesini yönetme.
    *   **Örnek Kullanıcı Hikayeleri:**
        *   *Bir kullanıcı olarak, favori mağazalarımı kaydedebilmek için sisteme üye olmak istiyorum.*
        *   *Bir kullanıcı olarak, favori AVM'lerimi profilimde görmek ve yönetmek istiyorum.*
        *   *Bir kullanıcı olarak, şifremi güvenli şekilde değiştirmek istiyorum.*
        *   *Bir admin olarak, kullanıcıların rollerini yönetebilmek istiyorum.*

*   **Yorum Servisi (Review Service):** Kullanıcıların AVM ve mağazalar hakkındaki yorumlarını ve puanlarını yönetir.
    *   **Özellikler:**
        *   AVM'ye yorum yapma ve puan verme (1-5).
        *   Mağazaya yorum yapma ve puan verme (1-5).
        *   Bir AVM/Mağaza için yorumları listeleme.
        *   Yorum onay mekanizması (Admin).

*   **Kampanya ve Etkinlik Servisi (Campaign & Event Service):** AVM ve mağazalardaki güncel kampanya ve etkinlikleri yönetir.
    *   **Özellikler:**
        *   Kampanyaları listeleme (filtreleme: AVM, mağaza, marka, kategori).
        *   Etkinlikleri listeleme (filtreleme: AVM, tarih aralığı, kategori).
        *   Kampanya ve etkinlik detaylarını görüntüleme.
        *   Kampanya ve etkinlik oluşturma/güncelleme işlemlerinde bitiş tarihi başlangıç tarihinden sonra olmalıdır (tarih aralığı validasyonu).

*   **Arama Servisi (Search Service):**
    *   **Özellikler:**
        *   Elasticsearch tabanlı serbest metin arama endpoint'i.
        *   Kullanıcıya özel favori AVM araması için user-service ile FeignClient entegrasyonu (favori AVM id'leri dinamik alınır).
        *   User-service erişilemezse fallback ile favori filtresi uygulanmaz, loglanır.
        *   Unit ve integration testler ile entegrasyon doğrulanır.
        *   Swagger/OpenAPI dokümantasyonu günceldir.
    *   **Güvenlik:**
        *   API Gateway'de JWT doğrulama zorunlu, sadece public endpointler hariç.
        *   Rate limiting için gateway'de ek yapılandırma ve/veya filter eklenmesi önerildi (örn: Redis tabanlı bucket, ileride eklenebilir).
        *   CORS ve CSRF korumaları SecurityConfig ile sağlanır.

*   **Bildirim Servisi (Notification Service):** Kullanıcılara ilgili güncellemeleri iletir.
    *   **Özellikler:** Favori mağaza/AVM kampanyaları, yeni etkinlikler hakkında anlık bildirimler (Push notification veya E-posta - sonraki fazda detaylandırılacak).

## 5. Teknik Gereksinimler

*   **Mimari:** Mikroservis Mimarisi (Spring Cloud ile API Gateway, Service Discovery, Config Server).
*   **Backend Dili/Framework:** Java (En son LTS veya güncel kararlı), Spring Boot (En son kararlı).
*   **Veritabanı:** PostgreSQL (Tercihen, GIS eklentisi ile) - Her mikroservis kendi DB'sine sahip olabilir.
*   **API:** RESTful API (JSON), DTO katmanı, API versiyonlama (/api/v1/...), Standart Hata Yönetimi.
    *   Tüm ana servislerde DTO validasyonları (@NotBlank, @Size, @Email, @Min, @Max, custom validator) eksiksiz uygulanmıştır.
    *   Swagger/OpenAPI dokümantasyonu tüm endpoint ve DTO'larda eksiksizdir.
*   **İletişim:** Senkron (REST) ve Asenkron (RabbitMQ/Kafka) iletişim.
*   **Test:** JUnit 5, Mockito, Testcontainers (Yüksek Unit ve Integration Test kapsamı).
*   **Kod Kalitesi:** SOLID, Clean Code, Design Patterns, DRY, KISS. Kodlama dili İngilizce.
*   **Güvenlik:** Spring Security (Authentication, Authorization).
*   **Diğer:** Lombok, MapStruct, Docker/Compose, CI/CD (GitHub Actions/GitLab CI), SLF4J/Logback, Redis/Hazelcast (Caching).

## 6. Fonksiyonel Olmayan Gereksinimler

*   **Performans:** API yanıt süreleri ortalama 500ms altında olmalıdır (yoğun olmayan yük altında). Konum bazlı sorgular optimize edilmelidir.
*   **Ölçeklenebilirlik:** Sistem, artan kullanıcı ve veri yükünü karşılayacak şekilde yatay olarak ölçeklenebilir olmalıdır (Mikroservislerin bağımsız ölçeklenmesi).
*   **Güvenilirlik/Kullanılabilirlik:** Servislerin çalışma süresi (uptime) %99.9 hedeflenmelidir. Kritik servisler için yedeklilik (redundancy) sağlanmalıdır.
*   **Güvenlik:**
    *   Tüm API endpoint'leri HTTPS üzerinden sunulmalıdır.
    *   Hassas veriler (şifreler vb.) güvenli bir şekilde saklanmalıdır (örn: BCrypt).
    *   Rol tabanlı yetkilendirme sıkı bir şekilde uygulanmalıdır.
    *   OWASP Top 10 zafiyetlerine karşı önlemler alınmalıdır (SQL Injection, XSS vb.).
    *   API Gateway rate limiting ve güvenlik duvarı (WAF) entegrasyonu değerlendirilmelidir.
*   **Bakım Kolaylığı:** Kod okunabilir, anlaşılır ve modüler olmalıdır. Bağımlılıklar güncel tutulmalıdır. Loglama ve metrik toplama etkin bir şekilde yapılmalıdır.
*   **Test Edilebilirlik:** Yüksek test kapsamı (%80+ unit test, anlamlı integration testler) hedeflenmektedir.

## 7. API Tasarım Felsefesi

*   RESTful prensiplerine tam uyum.
*   Anlaşılır ve tutarlı endpoint yapıları (Örn: `/api/v1/malls`, `/api/v1/malls/{mallId}/stores`).
*   Doğru HTTP metodlarının kullanımı (GET, POST, PUT, DELETE, PATCH).
*   Standart HTTP durum kodları ve anlamlı hata mesajları.
*   DTO katmanı ile içsel modellerin soyutlanması.
*   Versiyonlama (`/v1`, `/v2` vb.).
*   HATEOAS prensiplerinin değerlendirilmesi.

## 9. Proje Takibi

### Yapılacaklar (To Do)

**Altyapı ve Kurulum:**
*   [x] **Merkezi Loglama Sistemine Log Gönderimi Konfigürasyonu:** `logstash-logback-encoder` bağımlılığı tüm servislerde (`user-service`, `store-service`, `api-gateway`, `mall-service`) teyit edildi. `user-service`'ten kopyalanan standart `logback-spring.xml` dosyası (JSON formatında loglama ve potansiyel Logstash appender'ı ile) `store-service`, `api-gateway` ve `mall-service` servislerine eklendi. (Tam merkezi loglama (ELK/Loki) entegrasyonu ve `LogstashTcpSocketAppender` konfigürasyonu ileri bir adım olarak duruyor.)
*   [x] **Merkezi Konfigürasyon (`jwtSecret` Senkronizasyonu):** JWT ile ilgili ayarlar (`jwtSecret`, `jwtIssuer`, `jwtAudience` vb.) için `config-server` üzerinden merkezi yapılandırma prensibi benimsendi. `api-gateway` ve `store-service` içindeki `@Value` default değerleri standartlaştırıldı. Yerel `application.yml` dosyalarındaki doğrudan JWT tanımlamaları, `config-server`'dan alınacakları belirtilerek yorumlandı/kaldırıldı.
*   [x] **HTTPS Zorunluluğu (Production):** `user-service`'teki `JwtUtils` sınıfında, cookie'lerin `secure` flag'inin aktif Spring profiline (`prod` için `true`) göre dinamik olarak ayarlandığı teyit edildi. Bu prensip, cookie üreten diğer servisler için de geçerlidir.

**Güvenlik İyileştirmeleri (JWT & Auth):**
*   **Refresh Token Mekanizması Implementasyonu (`user-service`):**
    *   [x] Refresh token üretme mantığının `JwtUtils` veya yeni bir `RefreshTokenService` içine eklenmesi. (Daha uzun ömürlü, güvenli rastgele string, DB'de kullanıcı ile ilişkilendirilerek saklanabilir).
    *   [x] `/api/v1/auth/login` endpoint'inin, erişim token'ına ek olarak refresh token'ı da (ayrı bir `HttpOnly`, `Secure`, `SameSite` cookie içinde) döndürmesi.
    *   [x] Yeni bir `/api/v1/auth/refresh-token` endpoint'i oluşturulması: Bu endpoint geçerli bir refresh token (cookie'den okunacak) ile çağrıldığında yeni bir erişim token'ı (ve opsiyonel olarak yeni bir refresh token - rotation) üretip döndürmelidir.
    *   [x] Refresh token'ların veritabanında saklanması ve yönetilmesi (kullanıcıya bağlı, son kullanım tarihi, iptal durumu vb.).
    *   [x] Logout (`/api/v1/auth/logout`) sırasında refresh token'ın da hem cookie'den hem de veritabanından (veya blocklist'ten) geçersiz kılınması.
*   **JWT Claim Zenginleştirme:** JWT'lere iss, aud, jti claim'leri eklendi. Tüm servislerde bu claim'ler kontrol ediliyor.
*   **Token İmzası ve Algoritma Tutarlılığı:** Tüm servislerde aynı imza algoritması (HS256/HS512) ve secret kullanımı sağlandı, merkezi config ile yönetiliyor.
*   **Gelişmiş Token Geçersiz Kılma:** Refresh token'lar DB'de tutuluyor, logout ve şifre değişikliğinde blocklist uygulanıyor.

**`user-service` İyileştirmeleri:**
*   [x] **Favori AVM/Mağaza Yönetimi (User Service):** Kullanıcıların favori AVM ve mağazalarını ekleyip/listeleyip/silebileceği endpointler, entity/dto güncellemeleri, validasyon, Swagger/OpenAPI ve testler tamamlandı.
*   [x] AuthTokenFilter optimizasyonu:** user-service'de AuthTokenFilter artık doğrudan JWT'den user details oluşturuyor, gereksiz DB sorgusu yapılmıyor. Token'daki roller ve temel bilgiler kullanılıyor.

**API Gateway (`api-gateway`) İyileştirmeleri:**
*   [x] JWT issuer/audience kontrolü, rate limiting ve güvenlik ayarları tamamlandı.

**`store-service` İyileştirmeleri:**
*   [x] `UserDetailsServiceImpl.loadUserDetailsFromToken()` metodunun, `user-service`'teki `JwtUtils`'e eklenecek `iss`, `aud`, `jti` gibi yeni claim'leri de dikkate alacak şekilde güncellenmesi (gerekirse). (Mevcut `JwtUtils.validateJwtToken` içerisinde `iss` ve `aud` kontrolü yapıldığı için ek işlem gerekmedi.)
*   [x] PRD'deki diğer `Store` entity alanlarının eklenmesi ve ilgili CRUD/test güncellemeleri. (Alanların Entity ve DTO'larda zaten var olduğu, Mapper'ın bunları otomatik maplediği teyit edildi.)
*   [x] `CategoryDto` ve `CreateCategoryRequest` DTO'larındaki alan değişikliklerinin (örn: `parentId`, `iconUrl`) Entity, Mapper ve Service katmanlarına yansıtılması. (`Category` entity'sine `iconUrl` eklendi, Flyway script'i oluşturuldu, `CategoryMapper` güncellendi. DTO'larda alanlar zaten mevcuttu.)
*   [x] `store-service` için temel güvenlik yapılandırması (`SecurityConfig`) TODO olarak bırakılmıştı, bunun JWT cookie tabanlı sisteme tam entegrasyonunun tamamlanması. (Yapıldığı teyit edildi.)

**AVM Servisi (`mall-service`) Geliştirmeleri:**
*   [x] **AVM Hizmetleri (Facilities) Yönetimi:** AVM'nin sunduğu hizmetler (ör: danışma, mescit, ATM, eczane vb.) ayrı entity olarak modellendi. CRUD endpointleri, migration, DTO, validasyon, OpenAPI ve mapping işlemleri tamamlandı.

**Genel Kod Kalitesi ve Desenler:**
*   [x] **Kod Gözden Geçirme (`mall-service`):** `mall-service` özelinde SOLID prensiplerine uyum ve temel tasarım desenlerinin kullanımı analiz edildi. Mevcut yapının standartlara uygun olduğu, belirgin bir SRP ihlali olmadığı ve temel desenlerin (Repository, DTO, Mapper, Specification, Builder) etkin kullanıldığı değerlendirildi. Küçük iyileştirme alanları (örn: Swagger UI'da generic tip gösterimi) not edildi.
*   [x] **Kod Gözden Geçirme (Diğer Servisler):** Proje genelinde (özellikle `AuthServiceImpl`, `StoreServiceImpl`, `JwtUtils` gibi kritik sınıflarda) SRP, OCP prensiplerine uyumun artırılması için refactoring fırsatlarının değerlendirilmesi, gereksiz/opsiyonel/belirsiz yorumların temizlenmesi, gerekli yerlerde Strategy, Factory gibi desenlerin kullanımı ve kodun best practice'e uygun hale getirilmesi tamamlandı.
*   [ ] **Test Kapsamı:** Birim ve entegrasyon testlerinin gözden geçirilmesi, özellikle güvenlik ve yeni eklenen JWT akışları için test kapsamının artırılması.

**Diğer Servisler (İlerleyen Aşamalar):**
*   [x] **Yorum Servisi (Review Service):**
    *   [x] `review-service` modül yapısının oluşturulması.
    *   [x] Entity ve veritabanı tablolarının tasarlanması.
    *   [x] Repository, Service, ve Controller katmanlarının implemente edilmesi.
    *   [x] Yorum ekleme, listeleme, ve yönetme endpoint'lerinin oluşturulması.
    *   [x] Güvenlik katmanının (JWT doğrulama) entegre edilmesi.
*   [ ] `Kampanya ve Etkinlik Servisi` V1 geliştirilmesi.

**Kaldırılan/Tamamlanan (Önceki Yapılacaklardan):**
*   <strike>[ ] `Kullanıcı Servisi` V1 temel altyapısı oluşturuldu (Entity, Repo, Security Config, JWT Utils), ancak ilk etapta Register/Login endpointleri **kaldırıldı**.</strike> (Login/Register endpointleri artık aktif ve cookie tabanlı JWT ile çalışıyor)

### Yapılanlar (Done)

*   [x] PRD v0.1 oluşturuldu.
*   [x] PRD v0.2 detaylandırıldı.
*   [x] PRD v0.3 detaylandırıldı (Yapılacaklar gruplandı).
*   [x] Ana Maven `pom.xml` oluşturuldu.
*   [x] `discovery-server` modülü oluşturuldu.
*   [x] `config-server` modülü oluşturuldu.
*   [x] `api-gateway` modülü oluşturuldu (Temel Security dahil).
*   [x] `mall-service` modülü oluşturuldu (Temel Security, Flyway dahil).
*   [x] Merkezi konfigürasyon için Git repo yapısı ve örnek dosyalar tanımlandı.
*   [x] İstemci servislerinde `bootstrap.yml` kullanımı ve `spring.config.import` ayarlandı.
*   [x] `docker-compose.yml` dosyası oluşturuldu ve güncellendi (Postgres init, store-service).
*   [x] Mikroservisler için `Dockerfile`'lar oluşturuldu.
*   [x] `mall-service` için temel Entity, DTO, Mapper, Repository, Service, Controller ve Exception sınıfları oluşturuldu.
*   [x] `mall-service` için Service katmanı birim testleri (Mockito) eklendi.
*   [x] `mall-service` için Controller katmanı entegrasyon testleri (@SpringBootTest, Testcontainers) eklendi.
*   [x] `mall-service` için Repository katmanı entegrasyon testleri (@DataJpaTest, Testcontainers) eklendi.
*   [x] `mall-service` için Controller test hataları çözüldü.
*   [x] `mall-service` kod iyileştirmeleri yapıldı.
*   [x] `mall-service` için Flyway entegrasyonu yapıldı.
*   [x] Temel GitHub Actions CI pipeline (derleme ve test) oluşturuldu.
*   [x] `store-service` modülü oluşturuldu (Temel Security, Flyway dahil).
*   [x] `store-service` için temel Entity, DTO, Mapper, Repository, Service, Controller ve Exception sınıfları oluşturuldu.
*   [x] `store-service` Entity (`Category`, `Brand`, `Store`), Repository, DTO, Mapper, Service, Controller katmanları oluşturuldu/güncellendi.
*   [x] `store-service` için Flyway migration script'leri (`V2_categories`, `V3_brands`, `V4_stores`) oluşturuldu.
*   [x] `store-service` için Repository entegrasyon testleri (Testcontainers) oluşturuldu.
*   [x] `store-service` için Service birim testleri (Mockito) oluşturuldu.
*   [x] `store-service` için `GlobalExceptionHandler` ve özel exception sınıfları eklendi.
*   [x] `store-service` için `MallServiceClient` ile `mallId` validasyonu yapacak şekilde güncellendi.
*   [x] `store-service` için temel `SecurityConfig` oluşturuldu.
*   [x] `store-service` için SpringDoc API dokümantasyonu altyapısı eklendi (`springdoc-openapi-starter-webmvc-ui` ve `OpenApiConfig`).
*   [x] `store-service` Feign Client (`MallServiceClient`) için Resilience4J ile Circuit Breaker ve fallback mekanizması eklendi.
*   [x] `user-service` için `AuthTokenFilter` (JWT yetkilendirme filtresi) implemente edildi ve `SecurityConfig`'e eklendi.
*   [x] `store-service` için JWT filtreleme altyapısı (`AuthEntryPointJwt`, `JwtUtils`, `UserDetailsImpl`, `UserDetailsServiceImpl`, `AuthTokenFilter`) oluşturuldu ve `SecurityConfig` güncellendi.
*   [x] `api-gateway` için JWT doğrulama filtresi (`JwtUtilGateway`, `AuthenticationFilter`) oluşturuldu ve `X-Auth-Username`, `X-Auth-Roles` headerları eklendi.
*   [x] `store-service` Controller sınıflarına (`CategoryController`, `BrandController`, `StoreController`) ve bazı DTO'lara (`CategoryDto`, `CreateCategoryRequest`) SpringDoc/OpenAPI anotasyonları eklendi.
*   [x] `docker-compose.yml` dosyasına `user-service` eklendi.
*   [x] `user-service` güncellendi: JWT artık `HttpOnly` cookie olarak ayarlanıyor, `/login` yanıtı `UserPrincipalInfo` DTO'su içeriyor ve `/logout` endpoint'i eklendi.
*   [x] `user-service` içerisindeki `AuthTokenFilter`, JWT'yi `Authorization` header'ı yerine cookie'den okuyacak şekilde güncellendi.
*   [x] `user-service`, `api-gateway` ve `store-service` için `application.yml` dosyalarına (veya `JwtUtils` sınıflarına) `jwtCookieName` ayarı eklendi.
*   [x] `api-gateway` içerisindeki `AuthenticationFilter`, JWT'yi `Authorization` header'ı yerine cookie'den okuyacak şekilde güncellendi.
*   [x] `store-service` içerisindeki `JwtUtils` sınıfına cookie'den JWT okuma metodu eklendi ve `AuthTokenFilter`, JWT'yi cookie'den okuyacak şekilde güncellendi.
*   [x] **Observer Deseni:** `UserRegisteredEvent` için temel olay dinleme altyapısı (`UserRegisteredEvent`, `UserEventListener`, `AuthServiceImpl` güncellemeleri) `user-service` içinde oluşturuldu.
*   [x] **Strategy Deseni (Bildirimler - Temel Altyapı - `user-service`):** `NotificationStrategy`, `NotificationService` arayüzleri, `NotificationContext` DTO'su ve örnek bir `LogNotificationStrategy` oluşturuldu. `UserEventListener`, yeni kullanıcı kaydında bu altyapıyı kullanarak asenkron bildirim göndermeye (şimdilik loglama) başladı. `@EnableAsync` eklendi.
*   [x] **JWT Claim Zenginleştirme:** JWT'lere iss, aud, jti claim'leri eklendi. Tüm servislerde bu claim'ler kontrol ediliyor.
*   [x] **Token İmzası ve Algoritma Tutarlılığı:** Tüm servislerde aynı imza algoritması (HS256/HS512) ve secret kullanımı sağlandı, merkezi config ile yönetiliyor.
*   [x] **Gelişmiş Token Geçersiz Kılma:** Refresh token'lar DB'de tutuluyor, logout ve şifre değişikliğinde blocklist uygulanıyor.
*   [x] **API Gateway (`api-gateway`) İyileştirmeleri:** JWT issuer/audience kontrolü, rate limiting ve güvenlik ayarları tamamlandı.
*   [x] **AuthTokenFilter optimizasyonu:** user-service'de AuthTokenFilter artık doğrudan JWT'den user details oluşturuyor, gereksiz DB sorgusu yapılmıyor. Token'daki roller ve temel bilgiler kullanılıyor.
*   [x] **Arama için gerçek Elasticsearch mapping ve örnek veri:** Search-service için mapping, örnek veri ve bulk yükleme README ile tamamlandı.
*   [x] **Diğer mikroservislerden arama servisine veri senkronizasyonu:** Temel entegrasyon altyapısı hazırlandı.
*   [x] **Bildirim Servisi için push notification dummy implementasyonu:** Dummy push notification servisi ve event listener eklendi.
*   [x] **Monitoring & loglama eksiklerini kapatmak:** Örnek Grafana dashboard ve merkezi loglama konfigürasyonu eklendi.
*   [x] **API Gateway rate limiting ve güvenlik geliştirmeleri:** Temel rate limiting ve WAF entegrasyonu için hazırlıklar tamamlandı.
*   [x] **Mikroservislerden otomatik veri senkronizasyonu için event tabanlı otomasyon:** Mall-service AVM oluşturma eventini Kafka ile search-service'e iletiyor, search-service bu event ile Elasticsearch'e veri ekliyor.
*   [x] **Kullanıcıya özel favori AVM/mağaza arama ve öneri algoritması:** Search-service, onlyFavorites ve suggest parametreleri ile kullanıcıya özel arama ve öneri desteği sunuyor.
*   [x] **Arama sonuçlarında facet (şehir, kategori bazlı sayım) desteği:** Search-service, arama sonuçlarında cityCounts ve categoryCounts ile facet desteği sunuyor.
*   [x] **Arama servisi için user-service ile gerçek favori AVM entegrasyonu, fallback ve testler tamamlandı.**
*   [x] **API Gateway'de güvenlik ve rate limiting kontrolleri gözden geçirildi, JWT zorunluluğu ve public endpointler netleştirildi.**