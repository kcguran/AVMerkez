-- V4__user_favorites_tables.sql

CREATE TABLE user_favorite_malls (
    user_id BIGINT NOT NULL,
    mall_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, mall_id),
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
);

CREATE TABLE user_favorite_stores (
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, store_id),
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
); 