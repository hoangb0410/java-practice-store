CREATE TABLE redemptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    reward_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    points_deducted INTEGER NOT NULL,
    redemption_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);