CREATE TABLE IF NOT EXISTS auth_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    access_token VARCHAR(512),
    refresh_token VARCHAR(512),
    user_info VARCHAR(1024),
    oauth_provider VARCHAR(64)
);
