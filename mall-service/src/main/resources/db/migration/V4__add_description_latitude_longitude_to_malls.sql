-- Add description, latitude and longitude columns to malls table
ALTER TABLE malls
ADD COLUMN IF NOT EXISTS description TEXT,
ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION,
ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;

-- Create an index on lat/lon for better performance
CREATE INDEX IF NOT EXISTS idx_malls_latlon ON malls (latitude, longitude);

-- Comment on columns
COMMENT ON COLUMN malls.description IS 'Detailed description of the mall';
COMMENT ON COLUMN malls.latitude IS 'Geographic latitude coordinate';
COMMENT ON COLUMN malls.longitude IS 'Geographic longitude coordinate'; 