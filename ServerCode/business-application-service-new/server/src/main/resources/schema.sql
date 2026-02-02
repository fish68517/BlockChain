-- CollectorCoin Database Schema
-- Version: 1.0.0

CREATE DATABASE IF NOT EXISTS collectorcoin
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE collectorcoin;

-- Project Listing Table
CREATE TABLE IF NOT EXISTS project_listing (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_address VARCHAR(42),
    title VARCHAR(255),
    description TEXT,
    vin VARCHAR(50),
    value_estimation DECIMAL(18, 2),
    repair_estimation DECIMAL(18, 2),
    status VARCHAR(50),
    process_id BIGINT,
    nft_token_id BIGINT,
    nft_metadata_uri VARCHAR(500),
    nft_minted BOOLEAN DEFAULT FALSE,
    nft_launched BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_project_address (project_address),
    INDEX idx_process_id (process_id),
    INDEX idx_nft_token_id (nft_token_id)
) ENGINE=InnoDB;
