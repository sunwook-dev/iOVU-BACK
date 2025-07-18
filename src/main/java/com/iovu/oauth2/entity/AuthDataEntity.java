package com.iovu.oauth2.entity;

// JPA 엔티티 및 리포지토리 삭제, MyBatis용 매퍼 및 모델로 변경
public class AuthDataEntity {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String userInfo;
    private String oauthProvider;

    public AuthDataEntity() {}

    public AuthDataEntity(com.iovu.oauth2.dto.AuthDataDTO dto) {
        this.accessToken = dto.access_token;
        this.refreshToken = dto.refresh_token;
        this.userInfo = dto.user_info;
        this.oauthProvider = dto.oauth_provider;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getUserInfo() { return userInfo; }
    public void setUserInfo(String userInfo) { this.userInfo = userInfo; }
    public String getOauthProvider() { return oauthProvider; }
    public void setOauthProvider(String oauthProvider) { this.oauthProvider = oauthProvider; }
}
