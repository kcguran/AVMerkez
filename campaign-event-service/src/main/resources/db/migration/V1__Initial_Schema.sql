-- V1__Initial_Schema.sql

-- Create Campaigns Table
CREATE TABLE campaigns (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    mall_id BIGINT,
    store_id BIGINT,
    brand_id BIGINT,
    discount_type VARCHAR(100),
    conditions VARCHAR(500),
    campaign_code VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT check_end_date_after_start_date CHECK (end_date >= start_date)
);

-- Create Events Table
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    mall_id BIGINT NOT NULL, -- Events belong to a mall
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    description TEXT NOT NULL,
    location_description VARCHAR(255),
    category VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT check_event_end_date_after_start_date CHECK (end_date >= start_date)
);

-- Add Indexes for frequent lookups
CREATE INDEX idx_campaigns_mall_id ON campaigns(mall_id);
CREATE INDEX idx_campaigns_store_id ON campaigns(store_id);
CREATE INDEX idx_campaigns_brand_id ON campaigns(brand_id);
CREATE INDEX idx_campaigns_active ON campaigns(start_date, end_date);

CREATE INDEX idx_events_mall_id ON events(mall_id);
CREATE INDEX idx_events_active ON events(start_date, end_date); 