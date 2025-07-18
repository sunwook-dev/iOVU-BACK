package com.iovu.oauth2.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenBlacklistService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    // 실제 운영환경에서는 Redis나 데이터베이스 사용을 권장
    // 토큰과 만료시간을 함께 저장
    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    
    /**
     * 토큰을 블랙리스트에 추가
     * @param token JWT 토큰
     */
    public void blacklistToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.out.println("TokenBlacklistService: Empty token provided, skipping blacklist");
            return;
        }
        
        try {
            // 토큰의 만료시간을 가져와서 함께 저장
            long expirationTime = getTokenExpirationTime(token);
            blacklistedTokens.put(token, expirationTime);
            
            System.out.println("TokenBlacklistService: Token blacklisted successfully. Total blacklisted tokens: " + blacklistedTokens.size());
            System.out.println("TokenBlacklistService: Token expires at: " + new java.util.Date(expirationTime));
        } catch (Exception e) {
            System.err.println("TokenBlacklistService: Failed to blacklist token: " + e.getMessage());
            // 토큰 파싱에 실패해도 블랙리스트에 추가 (안전을 위해)
            // 기본 만료시간을 24시간으로 설정
            long defaultExpiry = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
            blacklistedTokens.put(token, defaultExpiry);
            System.out.println("TokenBlacklistService: Token added with default expiry: " + new java.util.Date(defaultExpiry));
        }
    }
    
    /**
     * 토큰이 블랙리스트에 있는지 확인
     * @param token 확인할 토큰
     * @return 블랙리스트에 있으면 true
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        return blacklistedTokens.containsKey(token);
    }
    
    /**
     * 만료된 토큰들을 주기적으로 제거 (매 시간마다 실행)
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void removeExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        int removedCount = 0;
        
        // 만료된 토큰들을 찾아서 제거
        blacklistedTokens.entrySet().removeIf(entry -> {
            if (entry.getValue() < currentTime) {
                return true;
            }
            return false;
        });
        
        if (removedCount > 0) {
            System.out.println("Removed " + removedCount + " expired tokens from blacklist");
        }
    }
    
    /**
     * 토큰의 만료시간을 가져옴
     */
    private long getTokenExpirationTime(String token) {
        try {
            // JWT 토큰을 직접 파싱하여 만료시간 추출
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getExpiration().getTime();
        } catch (Exception e) {
            // 파싱 실패 시 기본값 반환 (24시간 후)
            return System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        }
    }
    
    /**
     * 현재 블랙리스트된 토큰 수 반환 (모니터링용)
     */
    public int getBlacklistedTokenCount() {
        return blacklistedTokens.size();
    }
    
    /**
     * 모든 블랙리스트 클리어 (테스트용)
     */
    public void clearAllBlacklistedTokens() {
        blacklistedTokens.clear();
        System.out.println("All blacklisted tokens cleared");
    }
}
