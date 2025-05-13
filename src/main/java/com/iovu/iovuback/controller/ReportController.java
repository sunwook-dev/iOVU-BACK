package com.iovu.iovuback.controller;

import com.iovu.iovuback.dto.report.ReportDetailDto;
import com.iovu.iovuback.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    /** 보고서 상세 (날짜·AI 필터) */
    @GetMapping("/{id}")
    public ReportDetailDto detail(
            @PathVariable Long id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(required = false) String ai   // ← AiType → String
    ) {
        return service.detail(id, date, ai);           // ← 전달 타입一致
    }

    /** 보고서 삭제 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
