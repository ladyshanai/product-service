CREATE DATABASE IF NOT EXISTS product_service;
USE product_service;

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    product_type VARCHAR(30) NOT NULL,
    product_number VARCHAR(50) NOT NULL UNIQUE,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO product (
    customer_id,
    product_type,
    product_number,
    balance,
    active,
    created_at,
    updated_at
) VALUES
    (1, 'ACCOUNT', 'ACC-1001', 1200.50, TRUE, NOW(), NOW()),
    (2, 'CREDIT_CARD', 'CC-1002', 0.00, TRUE, NOW(), NOW()),
    (3, 'INVESTMENT', 'INV-1003', 350.75, TRUE, NOW(), NOW()),
    (4, 'ACCOUNT', 'ACC-2001', 9800.00, TRUE, NOW(), NOW()),
    (5, 'LOAN', 'LOAN-2002', 2150.30, TRUE, NOW(), NOW());
