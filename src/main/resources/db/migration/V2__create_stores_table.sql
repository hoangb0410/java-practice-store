CREATE TABLE stores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    otp VARCHAR(255),
    otp_expire_time INTEGER,
    is_verify BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);