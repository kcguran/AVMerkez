# AVMerkez Projesi - Ürün Gereksinim Dokümanı (PRD)

**Versiyon:** 0.2
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

*   **Mağaza Servisi (Store Service):** AVM'lerdeki mağazaları ve ilgili bilgileri yönetir.
    *   **Özellikler:**
        *   Bir AVM içindeki mağazaları listeleme (filtreleme: kategori, kat; arama: mağaza adı).
        *   Mağaza detaylarını görüntüleme (ad, kat, mağaza no, kategori, marka, iletişim, logo).
        *   Marka veya kategoriye göre farklı AVM'lerdeki mağazaları arama.
    *   **Örnek Kullanıcı Hikayeleri:**
        *   *Bir kullanıcı olarak, X AVM'sindeki elektronik mağazalarını listelemek istiyorum.*
        *   *Bir kullanıcı olarak, Y markasının hangi AVM'lerde mağazası olduğunu bulmak istiyorum.*

*   **Kategori ve Marka Servisi (Category & Brand Service):** Mağazaların sınıflandırılması için kategori ve marka bilgilerini yönetir.
    *   **Özellikler:**
        *   Kategori yönetimi (CRUD - Admin).
        *   Marka yönetimi (CRUD - Admin).
        *   Kategori ve marka listeleme (Kullanıcılar için).

*   **Kullanıcı Servisi (User Service):** Kullanıcı kimlik doğrulama, yetkilendirme ve profil yönetimini sağlar.
    *   **Özellikler:**
        *   E-posta/şifre ile kullanıcı kaydı.
        *   JWT (JSON Web Token) tabanlı kullanıcı girişi ve oturum yönetimi.
        *   Rol tabanlı yetkilendirme (USER, ADMIN, AVM_YONETICISI).
        *   Kullanıcı profili görüntüleme ve güncelleme (ad, soyad vb.).
        *   Favori AVM/Mağaza listesini yönetme.
    *   **Örnek Kullanıcı Hikayeleri:**
        *   *Bir kullanıcı olarak, favori mağazalarımı kaydedebilmek için sisteme üye olmak istiyorum.*
        *   *Bir kullanıcı olarak, favori AVM'lerimi profilimde görmek ve yönetmek istiyorum.*

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

*   **Arama Servisi (Search Service - Opsiyonel):** Tüm varlıklar üzerinde gelişmiş ve hızlı arama sağlar.
    *   **Özellikler:** AVM, mağaza, marka, kategori, kampanya, etkinlik metinlerinde serbest arama. (Elasticsearch ile implementasyon düşünülebilir).

*   **Bildirim Servisi (Notification Service):** Kullanıcılara ilgili güncellemeleri iletir.
    *   **Özellikler:** Favori mağaza/AVM kampanyaları, yeni etkinlikler hakkında anlık bildirimler (Push notification veya E-posta - sonraki fazda detaylandırılacak).

## 5. Teknik Gereksinimler

*   **Mimari:** Mikroservis Mimarisi (Spring Cloud ile API Gateway, Service Discovery, Config Server).
*   **Backend Dili/Framework:** Java (En son LTS veya güncel kararlı), Spring Boot (En son kararlı).
*   **Veritabanı:** PostgreSQL (Tercihen, GIS eklentisi ile) - Her mikroservis kendi DB'sine sahip olabilir.
*   **API:** RESTful API (JSON), DTO katmanı, API versiyonlama (/api/v1/...), Standart Hata Yönetimi.
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

## 8. Kapsam Dışı (Out of Scope - V1 için)

*   Mobil Uygulama (iOS/Android) arayüzleri.
*   Web Uygulaması arayüzü.
*   AVM Yöneticisi paneli ve detaylı yetkileri.
*   Mağaza Yöneticisi paneli.
*   Gelişmiş Raporlama ve Analitik özellikleri.
*   Ödeme sistemleri entegrasyonu (varsa).
*   Sosyal medya entegrasyonları (paylaşım vb.).
*   Anlık bildirim (Push Notification) altyapısının tam implementasyonu.

## 9. Proje Takibi

### Yapılacaklar (To Do)

*   [ ] Proje iskeletinin oluşturulması (Maven/Gradle).
*   [ ] Temel altyapı servislerinin kurulumu ve konfigürasyonu (API Gateway, Service Discovery, Config Server).
*   [ ] Docker ve Docker Compose yapılandırması.
*   [ ] Temel CI/CD pipeline kurulumu.
*   [ ] `AVM Servisi` V1 geliştirilmesi (CRUD, Temel Listeleme).
*   [ ] `Mağaza Servisi` V1 geliştirilmesi (CRUD, AVM'ye göre listeleme).
*   [ ] `Kullanıcı Servisi` V1 geliştirilmesi (Kayıt, Login - JWT).
*   [ ] `Kategori Servisi` V1 geliştirilmesi (CRUD).
*   [ ] `Marka Servisi` V1 geliştirilmesi (CRUD).
*   [ ] Mikroservisler arası temel iletişimin sağlanması (REST).
*   [ ] Global Exception Handling mekanizmasının implementasyonu.
*   [ ] Temel Spring Security yapılandırması.
*   [ ] PostgreSQL veritabanı şemalarının tasarlanması (Her servis için ayrı).
*   [ ] Veritabanı migration aracı entegrasyonu (Flyway/Liquibase).
*   [ ] Birim ve entegrasyon testlerinin yazılması (İlk servisler için).
*   [ ] Loglama altyapısının yapılandırılması.
*   [ ] API dokümantasyonu için Springdoc OpenAPI entegrasyonu.

### Yapılanlar (Done)

*   [x] PRD v0.1 oluşturuldu.
*   [x] PRD v0.2 detaylandırıldı.
