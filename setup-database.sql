-- iOVU 데이터베이스 설정 스크립트

-- 1. 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS iovu 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. 데이터베이스 사용
USE iovu;

-- 3. 사용자 생성 및 권한 부여 (필요한 경우)
-- CREATE USER IF NOT EXISTS 'iovu_user'@'localhost' IDENTIFIED BY 'iovu1234';
-- GRANT ALL PRIVILEGES ON iovu.* TO 'iovu_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 4. 인증 데이터 테이블 생성
CREATE TABLE IF NOT EXISTS auth_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    access_token VARCHAR(512),
    refresh_token VARCHAR(512),
    user_info VARCHAR(1024),
    oauth_provider VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5. 토큰 블랙리스트 테이블 생성
CREATE TABLE IF NOT EXISTS token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    blacklisted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL
);

-- 6. 사용자 정보 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(255),
    profile_image VARCHAR(512),
    oauth_provider VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 7. 인덱스 생성
CREATE INDEX idx_auth_data_provider ON auth_data(oauth_provider);
CREATE INDEX idx_token_blacklist_expires ON token_blacklist(expires_at);
CREATE INDEX idx_users_provider ON users(oauth_provider);

-- 8. 테이블 상태 확인
SHOW TABLES;
DESCRIBE auth_data;
DESCRIBE token_blacklist;
DESCRIBE users; 