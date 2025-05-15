package com.iovu.iovuback.controller;

import com.iovu.iovuback.service.DashboardPDFService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@Slf4j
public class DashboardExportController {
    @Autowired
    private DashboardPDFService service;

//    private Logger log = LoggerFactory.getLogger(DashboardExportController.class);
    /**
     * report_detail_id 기반으로 해당 report의 모든 AI 유형의 대시보드 PDF 다운로드
     */
    @GetMapping("/dashboard/{report_detail_id}/export/all")
    public ResponseEntity<byte[]> downloadAllAIDashboardPdf(@PathVariable Integer report_detail_id) {
        try {
            ByteArrayInputStream pdfStream = service.generateAllAIDashboardPdf(report_detail_id);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition",
                    "attachment; filename=dashboard_all_" + report_detail_id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfStream.readAllBytes());
        } catch (Exception e) {
            System.out.println("대시보드 PDF 다운로드 중 오류 발생: " + e.getMessage() + e);
//            log.error("대시보드 PDF 다운로드 중 오류 발생: {} ", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
