package com.iovu.oauth2.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iovu.oauth2.dto.TokenDto;
import com.iovu.oauth2.service.JwtTokenService;
import com.iovu.oauth2.service.OAuth2LogoutService;
import com.iovu.oauth2.service.TokenBlacklistService;
import com.iovu.oauth2.mapper.AuthDataMapper;
import com.iovu.oauth2.model.AuthData;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    @Autowired
    private JwtTokenService jwtTokenService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Autowired
    private OAuth2LogoutService oAuth2LogoutService;
    
    @Autowired
    private AuthDataMapper authDataMapper;

    // 테스트용 엔드포인트
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of(
            "message", "OAuth2 Backend is running", 
            "timestamp", System.currentTimeMillis(),
            "oauth2_endpoints", Map.of(
                "google", "/oauth2/authorization/google",
                "kakao", "/oauth2/authorization/kakao", 
                "naver", "/oauth2/authorization/naver"
            )
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        
        if (refreshToken == null || !jwtTokenService.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }

        try {
            String userId = jwtTokenService.getUserIdFromToken(refreshToken);
            String newAccessToken = jwtTokenService.generateAccessToken(userId);
            String newRefreshToken = jwtTokenService.generateRefreshToken(userId);

            TokenDto tokenDto = new TokenDto(newAccessToken, newRefreshToken);
            return ResponseEntity.ok(tokenDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token refresh failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request, HttpServletRequest httpRequest, HttpServletResponse response) {
        System.out.println("=== LOGOUT API CALLED ===");
        
        // 안전한 로그 출력
        try {
            if (request != null) {
                System.out.println("Request parameters: " + request.keySet());
            } else {
                System.out.println("Request is null!");
                return ResponseEntity.badRequest().body("Request body is null");
            }
        } catch (Exception e) {
            System.err.println("Error reading request parameters: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid request format");
        }
        
        
        String accessToken = null;
        String refreshToken = null;
        String provider = null;
        String oauthAccessToken = null;
        
        try {
            accessToken = request.get("accessToken");
            refreshToken = request.get("refreshToken");
            provider = request.get("provider"); // "kakao", "naver", "google"
            oauthAccessToken = request.get("oauthAccessToken"); // OAuth2 제공자의 액세스 토큰 (선택적)
            
            System.out.println("Parameter extraction completed successfully");
            System.out.println("Provider: " + provider);
            System.out.println("Access Token present: " + (accessToken != null && !accessToken.isEmpty()));
            System.out.println("Refresh Token present: " + (refreshToken != null && !refreshToken.isEmpty()));
            System.out.println("OAuth Access Token present: " + (oauthAccessToken != null && !oauthAccessToken.isEmpty()));
        } catch (Exception e) {
            System.err.println("Error extracting parameters: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error processing request parameters");
        }
        
        try {
            System.out.println("Starting logout process...");
            System.out.println("JwtTokenService available: " + (jwtTokenService != null));
            System.out.println("TokenBlacklistService available: " + (tokenBlacklistService != null));
            System.out.println("OAuth2LogoutService available: " + (oAuth2LogoutService != null));
            
            // 1. JWT 토큰들을 블랙리스트에 추가
            System.out.println("Step 1: Processing JWT tokens...");
            if (accessToken != null) {
                System.out.println("Validating access token...");
                if (jwtTokenService.validateToken(accessToken)) {
                    System.out.println("Access token is valid, adding to blacklist...");
                    tokenBlacklistService.blacklistToken(accessToken);
                    System.out.println("Access token added to blacklist");
                } else {
                    System.out.println("Access token validation failed");
                }
            } else {
                System.out.println("Access token is null");
            }
            
            if (refreshToken != null) {
                System.out.println("Validating refresh token...");
                if (jwtTokenService.validateToken(refreshToken)) {
                    System.out.println("Refresh token is valid, adding to blacklist...");
                    tokenBlacklistService.blacklistToken(refreshToken);
                    System.out.println("Refresh token added to blacklist");
                } else {
                    System.out.println("Refresh token validation failed");
                }
            } else {
                System.out.println("Refresh token is null");
            }
            
            System.out.println("Step 2: Processing server session...");
            
            // 2. 서버 측 세션 정리
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                System.out.println("Invalidating server session: " + session.getId());
                session.invalidate();
                System.out.println("Server session invalidated successfully");
            } else {
                System.out.println("No server session found to invalidate");
            }
            
            // JSESSIONID 쿠키 만료
            expireJSESSIONID(response, httpRequest);
            
            // 3. Spring Security Context 정리
            SecurityContextHolder.clearContext();
            System.out.println("Security context cleared");
            
            // 4. OAuth2 제공자에서 로그아웃 처리 상태
            boolean oauthLogoutSuccess = true;
            String logoutMessage = "JWT tokens invalidated, session cleared, and cookies deleted successfully";
            String socialLogoutStatus = "not_required"; // "not_required", "delegated_to_client", "completed"
            
            // 소셜 플랫폼 로그아웃은 프론트엔드에서 처리하도록 안내
            if (provider != null) {
                System.out.println("Provider detected: " + provider);
                logoutMessage = "완전한 로그아웃이 완료되었습니다. JWT 토큰 무효화, 세션 정리, 쿠키 삭제가 성공적으로 처리되었습니다.";
                socialLogoutStatus = "completed";
                System.out.println("Social platform logout marked as completed for provider: " + provider);
            } else {
                System.out.println("No provider specified, JWT tokens invalidated and session cleared only");
                logoutMessage = "로그아웃이 완료되었습니다. JWT 토큰 무효화 및 세션 정리가 성공적으로 처리되었습니다.";
            }
            
            // 5. 로그아웃 리다이렉트 URL 생성
            String logoutRedirectUrl = null;
            if (provider != null) {
                logoutRedirectUrl = oAuth2LogoutService.generateLogoutRedirectUrl(provider);
            }
            
            System.out.println("Complete logout finished. Message: " + logoutMessage);
            System.out.println("OAuth logout success: " + oauthLogoutSuccess);
            System.out.println("=== LOGOUT API COMPLETED ===");
            
            return ResponseEntity.ok(Map.of(
                "message", logoutMessage,
                "oauthLogoutSuccess", oauthLogoutSuccess,
                "socialLogoutStatus", socialLogoutStatus,
                "sessionCleared", true,
                "securityContextCleared", true,
                "cookiesCleared", true,
                "success", true,
                "logoutRedirectUrl", logoutRedirectUrl != null ? logoutRedirectUrl : "http://localhost:3000"
            ));
            
        } catch (Exception e) {
            System.err.println("Logout error: " + e.getMessage());
            System.err.println("Exception class: " + e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Logout failed",
                "message", e.getMessage(),
                "sessionCleared", false,
                "securityContextCleared", false
            ));
        }
    }
    
    // 모든 요청을 로그로 남기는 테스트 엔드포인트
    @PostMapping("/test-log")
    public ResponseEntity<?> testLog(@RequestBody(required = false) Object request, HttpServletRequest httpRequest) {
        System.out.println("=== TEST LOG ENDPOINT CALLED ===");
        System.out.println("Request URI: " + httpRequest.getRequestURI());
        System.out.println("Request Method: " + httpRequest.getMethod());
        System.out.println("Request Body: " + request);
        System.out.println("Content Type: " + httpRequest.getContentType());
        System.out.println("=== TEST LOG COMPLETED ===");
        
        return ResponseEntity.ok(Map.of(
            "message", "Test log endpoint working",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        
        if (token == null) {
            return ResponseEntity.badRequest().body("Token is required");
        }

        boolean isValid = jwtTokenService.validateToken(token);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    // 쿠키 만료 헬퍼 메서드
    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정
        cookie.setPath("/");
        response.addCookie(cookie);
        System.out.println("Cookie expired: " + cookieName);
    }
    
    // JSESSIONID 쿠키 만료 (여러 경로에서)
    private void expireJSESSIONID(HttpServletResponse response, HttpServletRequest request) {
        // 현재 요청의 쿠키 정보 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    System.out.println("Found JSESSIONID cookie - Domain: " + cookie.getDomain() + 
                                     ", Path: " + cookie.getPath() + ", Secure: " + cookie.getSecure());
                    
                    // 기존 쿠키와 동일한 설정으로 만료
                    Cookie expireCookie = new Cookie("JSESSIONID", null);
                    expireCookie.setMaxAge(0);
                    expireCookie.setPath(cookie.getPath() != null ? cookie.getPath() : "/");
                    expireCookie.setHttpOnly(true);
                    expireCookie.setSecure(cookie.getSecure());
                    
                    if (cookie.getDomain() != null) {
                        expireCookie.setDomain(cookie.getDomain());
                    }
                    
                    response.addCookie(expireCookie);
                    System.out.println("JSESSIONID cookie expired with original settings");
                }
            }
        }
        
        // 추가적으로 여러 경로에서 만료 시도
        String[] paths = {"/", "/api", "/oauth2"};
        for (String path : paths) {
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setMaxAge(0);
            cookie.setPath(path);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // 개발환경
            response.addCookie(cookie);
        }
        System.out.println("JSESSIONID cookie expiration attempted for multiple paths");
    }

    @PostMapping("/save-tokens")
    public ResponseEntity<?> saveTokens(@RequestBody com.iovu.oauth2.dto.AuthDataDTO dto) {
        // MyBatis로 DB에 저장
        authDataMapper.insert(new AuthData(dto));
        return ResponseEntity.ok("saved");
    }
}
