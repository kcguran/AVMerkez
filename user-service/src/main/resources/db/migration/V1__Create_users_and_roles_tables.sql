-- V1__Create_users_and_roles_tables.sql

-- Create users table (use app_users as 'user' might be reserved)
CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create user_roles table for mapping roles to users
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
);

-- Optional: Add indexes for performance
CREATE INDEX idx_users_username ON app_users(username);
CREATE INDEX idx_users_email ON app_users(email); 