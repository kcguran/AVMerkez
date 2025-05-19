-- V1__create_initial_schema.sql
-- Mağaza servisinin başlangıç şeması
-- Tablolar V2-V6 arası ayrı migration'larda oluşturulduğu için
-- bu dosya sadece eksik V1 migration hatasını gidermek için oluşturulmuştur

-- İzinlendirme ekle
COMMENT ON DATABASE store_db IS 'Store Service veritabanı';

-- İlk şema sürümünü işaretle
SELECT 1; 