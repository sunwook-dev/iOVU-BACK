package com.iovu.iovuback.util;

public class JwtUtil {
    // application.yml에 맞춘 시크릿 키
    private static final String SECRET_KEY = "iovu-development-secret-key-for-jwt-token-generation-must-be-at-least-256-bits-long";

    public static String getSecretKey() {
        return SECRET_KEY;
    }
}
