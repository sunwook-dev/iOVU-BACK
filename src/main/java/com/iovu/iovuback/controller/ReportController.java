package com.iovu.iovuback.controller;

import com.iovu.iovuback.domain.ReportEntity;
import com.iovu.iovuback.dto.ReportSummaryDTO;
import com.iovu.iovuback.dto.SocialLoginRequest;
import com.iovu.iovuback.service.ReportService;
import com.iovu.iovuback.service.SocialAuthService;
import com.iovu.iovuback.mapper.UserMapper;
import com.iovu.oauth2.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ReportController {
    private final ReportService reportService;
    private final SocialAuthService socialAuthService;
    private final UserMapper userMapper;
    
    @Autowired
    private JwtTokenService jwtTokenService;

    public ReportController(ReportService reportService, SocialAuthService socialAuthService, UserMapper userMapper) {
        this.reportService = reportService;
        this.socialAuthService = socialAuthService;
        this.userMapper = userMapper;
    }

    @GetMapping("/api/reports/{userUid}")
    public ResponseEntity<?> getReportSummariesByUser(@PathVariable Integer userUid) {
        try {
            List<ReportSummaryDTO> reports = reportService.getReportSummariesByUser(userUid);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reports", reports);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/report/{reportId}")
    public ResponseEntity<?> getReportDetail(@PathVariable Integer reportId) {
        ReportEntity report = reportService.getReportDetail(reportId);
        if (report == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("report", report);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/report")
    public ResponseEntity<?> registerReport(@RequestBody ReportEntity report) {
        int result = reportService.registerReport(report);
        if (result <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Report registration failed");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("inserted", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/auth/social")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequest request) {
        try {
            String jwt = socialAuthService.socialLogin(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/reports/me")
    public ResponseEntity<?> getMyReportSummaries(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Integer uid = io.jsonwebtoken.Jwts.parser()
                .setSigningKey(com.iovu.iovuback.util.JwtUtil.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("uid", Integer.class);
            List<ReportSummaryDTO> reports = reportService.getReportSummariesByUser(uid);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reports", reports);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/user-id")
    public ResponseEntity<?> getUserIdByProviderAndSocialId(@RequestParam String socialProvider, @RequestParam String socialId) {
        Integer userId = userMapper.selectUserIdByProviderAndSocialId(socialProvider, socialId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "User not found"));
        }
        return ResponseEntity.ok(Map.of("success", true, "userId", userId));
    }

    // JWT 토큰에서 사용자 ID를 가져오는 새로운 엔드포인트
    @GetMapping("/api/current-user-id")
    public ResponseEntity<?> getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false, 
                "message", "Authorization header with Bearer token is required"
            ));
        }

        String token = authHeader.substring(7);
        
        try {
            if (jwtTokenService.validateToken(token)) {
                String userId = jwtTokenService.getUserIdFromToken(token);
                return ResponseEntity.ok(Map.of("success", true, "userId", userId));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false, 
                    "message", "Invalid token"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false, 
                "message", "Token validation failed: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/api/report-id")
    public ResponseEntity<?> getReportIdByUserIdAndTitle(@RequestParam Integer userId, @RequestParam String reportTitle) {
        Integer reportId = userMapper.selectReportIdByUserIdAndTitle(userId, reportTitle);
        if (reportId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Report not found"));
        }
        return ResponseEntity.ok(Map.of("success", true, "reportId", reportId));
    }
}
