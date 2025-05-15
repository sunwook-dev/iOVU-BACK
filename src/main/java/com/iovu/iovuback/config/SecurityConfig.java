package com.iovu.iovuback.config;

import com.iovu.iovuback.handler.OAuth2LoginSuccessHandler; // OAuth2LoginSuccessHandler import 추가
import com.iovu.iovuback.service.auth.CustomOAuth2UserService; // CustomOAuth2UserService import
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService; // OAuth2 사용자 정보 처리 서비스
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler; // OAuth2 로그인 성공 핸들러

    // 아래 클래스들은 JWT 인증 필터 구현 시 필요합니다. (다음 단계에서 선택적으로 구현)
    // private final JwtUtil jwtUtil;
    // private final UserMapper userMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 보호 비활성화 (Stateless 서버에서는 보통 비활성화)
                .csrf(AbstractHttpConfigurer::disable)

                // 기본 HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // 기본 Form 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                // 세션 관리 정책: JWT를 사용할 것이므로 STATELESS로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청별 인가 규칙 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 다음 경로들은 인증 없이 접근 허용
                        .requestMatchers(
                                "/",
                                "/error", // 에러 페이지 (기본)
                                "/favicon.ico",
                                "/swagger-ui/**", // Swagger UI (사용하는 경우)
                                "/v3/api-docs/**", // Swagger API 문서 (사용하는 경우)
                                "/oauth2/**", // OAuth2 로그인 과정 관련 경로 (Spring Security가 처리)
                                "/login/oauth2/code/**" // OAuth2 콜백 경로
                        ).permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 사용자 정보 가져오고 처리할 서비스 지정
                        )
                        .successHandler(oAuth2LoginSuccessHandler) // 로그인 성공 시 JWT 발급 및 리디렉션 처리 핸들러 지정
                        // .failureHandler(...) // 로그인 실패 핸들러 (필요시 구현)
                );
                // .defaultSuccessUrl("/login-success") // successHandler를 사용하므로 이 줄은 삭제 또는 주석 처리합니다.

        // JWT 인증 필터를 사용한다면 여기에 추가 (UsernamePasswordAuthenticationFilter 전에)
        // http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userMapper), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정을 위한 Bean (React 앱과의 통신 허용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // TODO: React 앱의 실제 주소로 변경해주세요 (예: "http://localhost:3000")
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 프론트엔드 주소
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키/인증 정보 허용
        configuration.setMaxAge(3600L); // pre-flight 요청 캐시 시간(초)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 CORS 설정 적용
        return source;
    }
}