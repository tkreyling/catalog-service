ALTER TABLE product ADD category_id BIGINT;

ALTER TABLE product ADD CONSTRAINT fk_product_category_id FOREIGN KEY (category_id) REFERENCES category(id);