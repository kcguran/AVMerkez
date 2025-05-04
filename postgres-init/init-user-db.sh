-- Create databases if they don't exist
SELECT 'CREATE DATABASE mall_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'mall_db')\gexec
SELECT 'CREATE DATABASE store_db' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'store_db')\gexec 