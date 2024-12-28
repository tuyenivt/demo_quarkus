CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT,
    CONSTRAINT fk_product
        FOREIGN KEY(product_id)
            REFERENCES product(id)
            ON DELETE CASCADE
);
