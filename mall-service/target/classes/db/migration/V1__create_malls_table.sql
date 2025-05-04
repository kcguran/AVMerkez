-- Create the initial 'malls' table
CREATE TABLE malls (
    id BIGSERIAL PRIMARY KEY,          -- PostgreSQL için auto-incrementing primary key
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL
    -- PRD'deki diğer alanlar buraya eklenecek
    -- latitude DOUBLE PRECISION,
    -- longitude DOUBLE PRECISION,
    -- working_hours VARCHAR(255),
    -- website VARCHAR(255),
    -- phone VARCHAR(50)
);

-- İleride index eklemek gerekebilir
-- CREATE INDEX idx_malls_city ON malls (city); 