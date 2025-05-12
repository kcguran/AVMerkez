ALTER TABLE malls
DROP COLUMN IF EXISTS latitude,
DROP COLUMN IF EXISTS longitude,
ADD COLUMN location GEOMETRY(Point, 4326); -- SRID 4326 for WGS 84 (latitude/longitude)

-- Re-add other columns if they were part of the original V2 or ensure they are added separately
ALTER TABLE malls
ADD COLUMN IF NOT EXISTS working_hours VARCHAR(100),
ADD COLUMN IF NOT EXISTS website VARCHAR(255),
ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20),
ADD COLUMN IF NOT EXISTS services TEXT,
ADD COLUMN IF NOT EXISTS floor_plans TEXT,
ADD COLUMN IF NOT EXISTS popularity_score INTEGER; 