-- Create the 'stores' table
CREATE TABLE stores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    mall_id BIGINT NOT NULL, -- Foreign key constraint eklenebilir ama servisler ayrı DB'lerde olabilir
    floor VARCHAR(50) NOT NULL,
    store_number VARCHAR(50),
    category_id BIGINT,
    brand_id BIGINT,
    contact_info VARCHAR(255),
    description TEXT,
    logo_url VARCHAR(512)
);

-- Index for efficient lookup by mallId
CREATE INDEX idx_stores_mall_id ON stores (mall_id); 