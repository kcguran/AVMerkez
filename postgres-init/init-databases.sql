-- Create databases if they don't exist
-- Use standard CREATE DATABASE command; the entrypoint script handles errors if DB exists.

CREATE DATABASE mall_db;
CREATE DATABASE store_db;
CREATE DATABASE user_db;

-- Grant privileges if needed (example)
-- GRANT ALL PRIVILEGES ON DATABASE mall_db TO user;
-- GRANT ALL PRIVILEGES ON DATABASE store_db TO user; 