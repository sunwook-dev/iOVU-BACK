package com.iovu.iovuback.controller;


import com.iovu.iovuback.service.ReportPDFService;
import com.itextpdf.text.DocumentException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@Log4j2
public class ReportExportController {

    @Autowired
    private ReportPDFService pdfService;

    @GetMapping("/reports/{report_detail_id}/export")
    public ResponseEntity<byte[]> downloadReportPdf(@PathVariable Integer report_detail_id)
            throws IOException, DocumentException {
        try {
            ByteArrayInputStream pdfStream = pdfService.generateReportPdf(report_detail_id);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition",
                    "attachment; filename=domain_report_" + report_detail_id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfStream.readAllBytes());
        } catch (Exception e) {
            log.error("PDF 다운로드 중 오류 발생: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
