CREATE TABLE IF NOT EXISTS tb_coupon (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    creator_id BIGINT NOT NULL,
    discount_percentage NUMERIC(5,2) NOT NULL,
    active BOOLEAN NOT NULL
);

ALTER TABLE tb_order
    ADD COLUMN IF NOT EXISTS coupon_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_order_coupon'
    ) THEN
        ALTER TABLE tb_order
            ADD CONSTRAINT fk_order_coupon
                FOREIGN KEY (coupon_id)
                    REFERENCES tb_coupon(id);
    END IF;
END $$;
