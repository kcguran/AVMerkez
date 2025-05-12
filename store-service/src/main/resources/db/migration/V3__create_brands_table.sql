CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(255),
    website VARCHAR(255)
);

CREATE INDEX idx_brand_name ON brands(name); 