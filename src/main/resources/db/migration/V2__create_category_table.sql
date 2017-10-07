CREATE TABLE category (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY,
	name VARCHAR(255) NOT NULL,
	parent_category_id BIGINT,

    PRIMARY KEY (id),
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_category_id) REFERENCES category(id)
);
