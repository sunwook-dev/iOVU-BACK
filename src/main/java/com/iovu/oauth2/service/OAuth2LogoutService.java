package com.iovu.oauth2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuth2LogoutService {
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public boolean logoutFromKakao(String accessToken) {
        System.out.println("OAuth2LogoutService: Attempting Kakao logout");
        try {
            String url = "https://kapi.kakao.com/v1/user/logout";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBearerAuth(accessToken);
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", kakaoClientId);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            System.out.println("OAuth2LogoutService: Kakao logout successful");
            return true;
        } catch (Exception e) {
            // 로그 기록 후 false 반환
            System.err.println("OAuth2LogoutService: Kakao logout failed: " + e.getMessage());
            return false;
        }
    }
    
    public boolean logoutFromNaver(String accessToken) {
        System.out.println("OAuth2LogoutService: Attempting Naver logout");
        try {
            String url = "https://nid.naver.com/oauth2.0/token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "delete");
            params.add("client_id", naverClientId);
            params.add("client_secret", naverClientSecret);
            params.add("access_token", accessToken);
            params.add("service_provider", "NAVER");
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            System.out.println("OAuth2LogoutService: Naver logout successful");
            return true;
        } catch (Exception e) {
            // 로그 기록 후 false 반환
            System.err.println("OAuth2LogoutService: Naver logout failed: " + e.getMessage());
            return false;
        }
    }
    
    public String generateLogoutRedirectUrl(String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                // 카카오는 서버 간 로그아웃 후 백엔드 콜백 호출
                return "https://kauth.kakao.com/oauth/logout" +
                       "?client_id=" + kakaoClientId +
                       "&logout_redirect_uri=http://localhost:8081/api/auth/logout/callback";
            case "naver":
                // 네이버는 클라이언트 사이드에서 처리 (서버 로그아웃 API 제한적)
                return "http://localhost:3000/logout?provider=naver&status=success";
            case "google":
                // 구글도 클라이언트 사이드에서 처리 (Google Sign-In JavaScript API 사용)
                return "http://localhost:3000/logout?provider=google&status=success";
            default:
                return "http://localhost:3000/logout?status=success";
        }
    }
}
