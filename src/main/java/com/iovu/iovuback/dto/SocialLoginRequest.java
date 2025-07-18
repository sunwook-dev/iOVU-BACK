package com.iovu.iovuback.dto;

import lombok.Data;

@Data
public class SocialLoginRequest {
    private String provider;
    private String accessToken;
    private String refreshToken;
    private String userId;
    // 필요에 따라 필드 추가
}
