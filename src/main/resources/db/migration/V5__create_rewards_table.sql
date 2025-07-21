CREATE TABLE rewards (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL REFERENCES stores(id),
    name VARCHAR(255) NOT NULL,
    points_required INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    expiration_date DATE NOT NULL,
    quantity INTEGER NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);