package com.iovu.iovuback.controller;


import com.iovu.iovuback.domain.DomainLink;
import com.iovu.iovuback.domain.LinkList;
import com.iovu.iovuback.service.DomainLinkService;
import com.iovu.iovuback.service.ReportPDFService;
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
import java.util.List;

@RestController
@Log4j2
public class DomainLinkController {
    @Autowired
    private DomainLinkService service;
    @Autowired
    private ReportPDFService pdfService;

    @GetMapping("/reports/{report_detail_id}/links")
    public List<DomainLink> getDomainBYDetail(@PathVariable int report_detail_id) {
        return service.getDomainBYDetail(report_detail_id);
    }

    @GetMapping("/reports/{report_detail_id}/links/export")
    public ResponseEntity<List<LinkList>> getAllLinks(@PathVariable Integer report_detail_id) {
        List<LinkList> links = service.getLinkByReportDetail(report_detail_id);
        return ResponseEntity.ok(links);
    }
    @GetMapping("/reports/{report_detail_id}/domains/export")
    public ResponseEntity<List<DomainLink>> getAllDomain(@PathVariable Integer report_detail_id) {
        List<DomainLink> domain = service.getDomainByReportDetail(report_detail_id);
        return ResponseEntity.ok(domain);
    }

    /**
     * 링크 목록 조회 (JSON 반환)
     */
//    @GetMapping("/reports/{report_detail_id}/links")
//    public ResponseEntity<List<LinkList>> getAllLinks(@PathVariable Integer report_detail_id) {
//        List<LinkList> links = service.getLinkByReportDetail(report_detail_id);
//        return ResponseEntity.ok(links);
//    }
//
    /**
     * 도메인 링크 목록 조회 (JSON 반환)
     */
//    @GetMapping("/reports/{report_detail_id}/domain")
//    public ResponseEntity<List<DomainLink>> getAllDomain(@PathVariable Integer report_detail_id) {
//        List<DomainLink> domain = service.getDomainByReportDetail(report_detail_id);
//        return ResponseEntity.ok(domain);
//    }

    /**
     * 링크 목록 PDF로 다운로드
     */
    @GetMapping("/reports/{report_detail_id}/links/export/pdf")
    public ResponseEntity<byte[]> downloadLinksPdf(@PathVariable Integer report_detail_id) {
        try {
            // 링크 목록 데이터 조회
            List<LinkList> links = service.getLinkByReportDetail(report_detail_id);

            if (links == null || links.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            // PDF 생성
            ByteArrayInputStream pdfStream = pdfService.createDomainLinkPdf(report_detail_id);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=links_" + report_detail_id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfStream.readAllBytes());
        } catch (Exception e) {
            log.error("링크 PDF 다운로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 도메인 링크 목록 PDF로 다운로드
     */
    @GetMapping("/reports/{report_detail_id}/domains/export/pdf")
    public ResponseEntity<byte[]> downloadDomainPdf(@PathVariable Integer report_detail_id) {
        try {
            // 도메인 링크 데이터 조회
            List<DomainLink> domains = service.getDomainByReportDetail(report_detail_id);

            if (domains == null || domains.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            // PDF 생성
            ByteArrayInputStream pdfStream = pdfService.createDomainLinkPdf(report_detail_id);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=domains_" + report_detail_id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfStream.readAllBytes());
        } catch (Exception e) {
            log.error("도메인 PDF 다운로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
