CREATE TABLE users_stores (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    store_id BIGINT NOT NULL REFERENCES stores(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);