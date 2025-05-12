CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    parent_category_id BIGINT,
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_category_id) REFERENCES categories(id)
);

CREATE INDEX idx_category_name ON categories(name);
CREATE INDEX idx_category_parent_id ON categories(parent_category_id); 