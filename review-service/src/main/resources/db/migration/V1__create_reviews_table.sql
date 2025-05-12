CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    mall_id BIGINT,
    store_id BIGINT,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    approval_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    approved_at TIMESTAMP WITH TIME ZONE,
    approved_by BIGINT,
    rejection_reason VARCHAR(500),
    CONSTRAINT chk_mall_store_exclusive CHECK (
        (mall_id IS NOT NULL AND store_id IS NULL) OR
        (mall_id IS NULL AND store_id IS NOT NULL)
    )
);

-- İndeksler
CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_mall_id ON reviews(mall_id);
CREATE INDEX idx_reviews_store_id ON reviews(store_id);
CREATE INDEX idx_reviews_approval_status ON reviews(approval_status);
CREATE INDEX idx_reviews_created_at ON reviews(created_at);

-- Yorum
COMMENT ON TABLE reviews IS 'AVM ve mağazalar için kullanıcı yorumları ve puanları';
COMMENT ON COLUMN reviews.user_id IS 'Yorumu yapan kullanıcının ID''si';
COMMENT ON COLUMN reviews.mall_id IS 'Yorum yapılan AVM''nin ID''si (eğer yorum bir AVM için yapıldıysa)';
COMMENT ON COLUMN reviews.store_id IS 'Yorum yapılan mağazanın ID''si (eğer yorum bir mağaza için yapıldıysa)';
COMMENT ON COLUMN reviews.rating IS 'Puanlama (1-5)';
COMMENT ON COLUMN reviews.comment IS 'Yorum metni (opsiyonel)';
COMMENT ON COLUMN reviews.approval_status IS 'Onay durumu (PENDING, APPROVED, REJECTED)';
COMMENT ON COLUMN reviews.approved_at IS 'Onay/red tarihi';
COMMENT ON COLUMN reviews.approved_by IS 'Onaylayan/reddeden admin ID''si';
COMMENT ON COLUMN reviews.rejection_reason IS 'Reddedilme nedeni (eğer reddedildiyse)'; 