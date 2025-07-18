package com.iovu.iovuback.service;

import com.iovu.iovuback.dto.SocialLoginRequest;
import org.springframework.stereotype.Service;

@Service
public class SocialAuthService {
    public String socialLogin(SocialLoginRequest request) {
        // TODO: 실제 소셜 로그인 처리 및 JWT 발급 로직 구현
        return "dummy-jwt-token";
    }
}
