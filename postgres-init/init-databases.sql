-- Veritabanlarını oluştur
CREATE DATABASE mall_db;
CREATE DATABASE store_db;
CREATE DATABASE user_db;
CREATE DATABASE review_db;
CREATE DATABASE campaign_event_db;

-- Yetkileri düzenle
GRANT ALL PRIVILEGES ON DATABASE mall_db TO "user";
GRANT ALL PRIVILEGES ON DATABASE store_db TO "user";
GRANT ALL PRIVILEGES ON DATABASE user_db TO "user";
GRANT ALL PRIVILEGES ON DATABASE review_db TO "user";
GRANT ALL PRIVILEGES ON DATABASE campaign_event_db TO "user";

-- Her veritabanında PostGIS eklentisini etkinleştir
\c mall_db
CREATE EXTENSION IF NOT EXISTS postgis;

\c store_db
CREATE EXTENSION IF NOT EXISTS postgis;

\c user_db
CREATE EXTENSION IF NOT EXISTS postgis;

\c review_db
CREATE EXTENSION IF NOT EXISTS postgis;

\c campaign_event_db
CREATE EXTENSION IF NOT EXISTS postgis; 