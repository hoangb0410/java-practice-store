CREATE TABLE ranks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    points_threshold INTEGER NOT NULL,
    amount INTEGER NOT NULL,
    fixed_point INTEGER NOT NULL,
    percentage FLOAT NOT NULL,
    max_percentage_points INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);