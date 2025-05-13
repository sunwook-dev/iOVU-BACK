package com.iovu.iovuback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<?> handleReportNotFound(ReportNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 404);
        body.put("error", "Report not found");
        body.put("message", ex.getMessage());
        body.put("path", "/reports/{id}");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
