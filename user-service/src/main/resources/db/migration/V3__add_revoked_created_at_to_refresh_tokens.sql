ALTER TABLE refresh_tokens ADD COLUMN revoked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE refresh_tokens ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW();

COMMENT ON COLUMN refresh_tokens.revoked IS 'Token iptal edilmiş mi';
COMMENT ON COLUMN refresh_tokens.created_at IS 'Tokenın oluşturulma tarihi'; 