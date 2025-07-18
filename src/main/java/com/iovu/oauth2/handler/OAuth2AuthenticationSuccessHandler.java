package com.iovu.oauth2.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iovu.oauth2.dto.UserInfoDto;
import com.iovu.oauth2.service.JwtTokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenService jwtTokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = extractRegistrationId(request);
        
        // 사용자 정보 추출
        UserInfoDto userInfo = extractUserInfo(oAuth2User, registrationId);
        
        // JWT 토큰 생성
        String accessToken = jwtTokenService.generateAccessToken(userInfo.getId());
        String refreshToken = jwtTokenService.generateRefreshToken(userInfo.getId());
        
        // 프론트엔드로 리다이렉트 (프론트엔드 요구사항에 맞는 파라미터명 사용)
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/callback")
                .queryParam("access_token", accessToken)
                .queryParam("refresh_token", refreshToken)
                .queryParam("user_info", URLEncoder.encode(objectMapper.writeValueAsString(userInfo), StandardCharsets.UTF_8))
                .queryParam("oauth_provider", registrationId)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String extractRegistrationId(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] pathSegments = requestURI.split("/");
        return pathSegments[pathSegments.length - 1];
    }

    private UserInfoDto extractUserInfo(OAuth2User oAuth2User, String registrationId) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        switch (registrationId.toLowerCase()) {
            case "google":
                return new UserInfoDto(
                    (String) attributes.get("sub"),
                    (String) attributes.get("name"),
                    (String) attributes.get("email"),
                    "google",
                    (String) attributes.get("picture")
                );
            
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                return new UserInfoDto(
                    String.valueOf(attributes.get("id")),
                    (String) profile.get("nickname"),
                    null, // 이메일 권한이 없으므로 null로 설정
                    "kakao",
                    (String) profile.get("profile_image_url")
                );
            
            case "naver":
                Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
                return new UserInfoDto(
                    (String) naverResponse.get("id"),
                    (String) naverResponse.get("name"),
                    (String) naverResponse.get("email"),
                    "naver",
                    (String) naverResponse.get("profile_image")
                );
            
            default:
                throw new IllegalArgumentException("Unknown registration id: " + registrationId);
        }
    }
}
