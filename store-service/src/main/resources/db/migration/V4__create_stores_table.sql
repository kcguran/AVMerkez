CREATE TABLE stores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    mall_id BIGINT NOT NULL, -- No foreign key here, as mall-service is separate
    floor VARCHAR(50),
    store_number VARCHAR(50),
    category_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    contact_information TEXT,
    description TEXT,
    logo_url VARCHAR(255),
    CONSTRAINT fk_store_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_store_brand FOREIGN KEY (brand_id) REFERENCES brands(id)
);

CREATE INDEX idx_store_name ON stores(name);
CREATE INDEX idx_store_mall_id ON stores(mall_id);
CREATE INDEX idx_store_category_id ON stores(category_id);
CREATE INDEX idx_store_brand_id ON stores(brand_id);
CREATE INDEX idx_store_mall_category ON stores(mall_id, category_id);
CREATE INDEX idx_store_mall_brand ON stores(mall_id, brand_id); 