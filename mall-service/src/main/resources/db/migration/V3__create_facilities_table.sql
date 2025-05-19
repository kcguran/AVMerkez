CREATE TABLE facilities (
    id BIGSERIAL PRIMARY KEY,
    mall_id BIGINT NOT NULL REFERENCES malls(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    floor VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_facilities_mall_id ON facilities(mall_id); 