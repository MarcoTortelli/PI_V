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
