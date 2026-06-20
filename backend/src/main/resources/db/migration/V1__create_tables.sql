CREATE TABLE tb_user (
                         id BIGSERIAL PRIMARY KEY,
                         login VARCHAR(255),
                         password VARCHAR(255),
                         role_type VARCHAR(50),
                         created_at TIMESTAMP
);

CREATE TABLE tb_product (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255),
                            price NUMERIC(19,2),
                            quantity INTEGER,
                            image_url VARCHAR(1000)
);

CREATE TABLE tb_coupon (
                           id BIGSERIAL PRIMARY KEY,
                           code VARCHAR(50) UNIQUE NOT NULL,
                           expiration_date TIMESTAMP NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           creator_id BIGINT NOT NULL,
                           discount_percentage NUMERIC(5,2) NOT NULL,
                           active BOOLEAN NOT NULL
);

CREATE TABLE tb_payment (
                            id BIGSERIAL PRIMARY KEY,
                            order_id BIGINT,
                            payment_method VARCHAR(50),
                            amount_paid NUMERIC(19,2),
                            card_information VARCHAR(255)
);

CREATE TABLE tb_order (
                          id BIGSERIAL PRIMARY KEY,
                          created_at TIMESTAMP,
                          total NUMERIC(19,2),
                          paid BOOLEAN,
                          payment_id BIGINT,
                          user_id BIGINT,
                          coupon_id BIGINT
);

CREATE TABLE tb_order_item (
                               id BIGSERIAL PRIMARY KEY,
                               product_id BIGINT,
                               quantity INTEGER,
                               price_at_purchase NUMERIC(19,2),
                               cart_id BIGINT
);

ALTER TABLE tb_order
    ADD CONSTRAINT fk_order_user
        FOREIGN KEY (user_id)
            REFERENCES tb_user(id);

ALTER TABLE tb_order
    ADD CONSTRAINT fk_order_coupon
        FOREIGN KEY (coupon_id)
            REFERENCES tb_coupon(id);

ALTER TABLE tb_order
    ADD CONSTRAINT fk_order_payment
        FOREIGN KEY (payment_id)
            REFERENCES tb_payment(id);

ALTER TABLE tb_order_item
    ADD CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id)
            REFERENCES tb_product(id);

ALTER TABLE tb_order_item
    ADD CONSTRAINT fk_order_item_order
        FOREIGN KEY (cart_id)
            REFERENCES tb_order(id);

ALTER TABLE tb_payment
    ADD CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id)
            REFERENCES tb_order(id);