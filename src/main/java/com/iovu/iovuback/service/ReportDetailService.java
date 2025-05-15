package com.iovu.iovuback.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iovu.iovuback.domain.ReportDetail;
import com.iovu.iovuback.mapper.ReportDetailMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
public class ReportDetailService {

    @Autowired
    private ReportDetailMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * ID로 ReportDetail 조회
     */
    public ReportDetail getReportDetailById(int reportDetailId) {
        return mapper.getReportDetailById(reportDetailId);
    }

    /**
     * Report ID로 ReportDetail 목록 조회
     */
//    public List<ReportDetail> getReportDetailsByReportId(Integer report_id) {
//        return mapper.getReportDetailsByReportId(report_id);
//    }

    /**
     * ReportDetail 저장
     */
//    @Transactional
//    public int saveReportDetail(ReportDetail reportDetail) {
//        if (reportDetail.getReport_detail_id() == null) {
//            return mapper.insertReportDetail(reportDetail);
//        } else {
//            return reportDetailMapper.updateReportDetail(reportDetail);
//        }
//    }

    /**
     * ReportDetail을 PDF로 변환
     */
    public byte[] generatePdf(int reportDetailId) throws DocumentException, IOException {
        ReportDetail reportDetail = mapper.getReportDetailById(reportDetailId);
        if (reportDetail == null) {
            throw new RuntimeException("ReportDetail not found with ID: " + reportDetailId);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // 폰트 설정 (한글 지원을 위해 BaseFont 사용)
        BaseFont baseFont = BaseFont.createFont("NanumGothic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(baseFont, 18, Font.BOLD);
        Font subtitleFont = new Font(baseFont, 14, Font.BOLD);
        Font normalFont = new Font(baseFont, 12, Font.NORMAL);

        document.open();

        // 제목 추가
        Paragraph title = new Paragraph("리포트 상세 정보", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // 기본 정보 테이블 생성
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {1, 3});
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

//        addTableCellSafely (table, "리포트 ID", String.valueOf(reportDetail.getReport_id()), subtitleFont, normalFont);
//        addTableCellSafely (table, "AI 유형", reportDetail.getAi_type(), subtitleFont, normalFont);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = reportDetail.getCreated_at() != null ?
//                sdf.format(reportDetail.getCreated_at()) : "날짜 정보 없음";
//        addTableCellSafely (table, "생성 일시", formattedDate, subtitleFont, normalFont);
//
        document.add(table);

        // JSON 데이터 추가
        if (reportDetail.getJson_data() != null && !reportDetail.getJson_data().isEmpty()) {
            document.add(new Paragraph("\nJSON 데이터", subtitleFont));

            try {
                // JSON 데이터 파싱 및 표시
                Map<String, Object> jsonData = objectMapper.readValue(reportDetail.getJson_data(), Map.class);
                PdfPTable jsonTable = new PdfPTable(2);
                jsonTable.setWidthPercentage(100);
                jsonTable.setWidths(new float[] {1, 3});

//                for (Map.Entry<String, Object> entry : jsonData.entrySet()) {
//                    addTableCell(jsonTable, entry.getKey(), entry.getValue().toString(), normalFont, normalFont);
//                }

                document.add(jsonTable);
            } catch (Exception e) {
                // JSON 파싱 실패 시 원본 텍스트로 표시
                document.add(new Paragraph(reportDetail.getJson_data(), normalFont));
            }
        }

//        // 컨설팅 정보 추가
//        if (reportDetail.getConsulting() != null) {
//            document.add(new Paragraph("\n컨설팅 정보", subtitleFont));
//            PdfPTable consultingTable = new PdfPTable(2);
//            consultingTable.setWidthPercentage(100);
//            consultingTable.setWidths(new float[] {1, 3});
//
//            addTableCell(consultingTable, "컨설팅 ID",
//                    String.valueOf(reportDetail.getConsulting().getConsulting_id()), normalFont, normalFont);
//            addTableCell(consultingTable, "컨설팅 이름",
//                    reportDetail.getConsulting().getConsulting_name(), normalFont, normalFont);
//            addTableCell(consultingTable, "컨설팅 설명",
//                    reportDetail.getConsulting().getConsulting_description(), normalFont, normalFont);
//
//            document.add(consultingTable);
//        }
//
//        // 도메인 링크 정보 추가
//        if (reportDetail.getDomain_link() != null) {
//            document.add(new Paragraph("\n도메인 링크 정보", subtitleFont));
//            PdfPTable domainTable = new PdfPTable(2);
//            domainTable.setWidthPercentage(100);
//            domainTable.setWidths(new float[] {1, 3});
//
//            addTableCell(domainTable, "도메인 ID",
//                    String.valueOf(reportDetail.getDomain_link().getDomain_link_id()), normalFont, normalFont);
//            addTableCell(domainTable, "도메인 이름",
//                    reportDetail.getDomain_link().getDomain_name(), normalFont, normalFont);
//            addTableCell(domainTable, "도메인 URL",
//                    reportDetail.getDomain_link().getDomain_url(), normalFont, normalFont);
//
//            document.add(domainTable);
//        }

//        // 대시보드 정보 추가
//        if (reportDetail.getDashboard() != null) {
//            document.add(new Paragraph("\n대시보드 정보", subtitleFont));
//            PdfPTable dashboardTable = new PdfPTable(2);
//            dashboardTable.setWidthPercentage(100);
//            dashboardTable.setWidths(new float[] {1, 3});
//
//            addTableCell(dashboardTable, "대시보드 ID",
//                    String.valueOf(reportDetail.getDashboard().getDashboard_id()), normalFont, normalFont);
//            addTableCell(dashboardTable, "대시보드 이름",
//                    reportDetail.getDashboard().getDashboard_name(), normalFont, normalFont);
//            addTableCell(dashboardTable, "대시보드 설정",
//                    reportDetail.getDashboard().getDashboard_config(), normalFont, normalFont);
//
//            document.add(dashboardTable);
//        }
//
        // 페이지 번호 추가
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                Phrase footer = new Phrase(String.format("페이지 %d", writer.getPageNumber()), normalFont);

                float footerX = (document.right() - document.left()) / 2 + document.leftMargin();
                float footerY = document.bottom() - 20;

                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, footerX, footerY, 0);
            }
        });

        document.close();
        return baos.toByteArray();
    }

    /**
     * PDF 테이블에 셀 추가 헬퍼 메소드
     */
//    private void addTableCell(PdfPTable table, String key, String value, Font keyFont, Font valueFont) {
//        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
//        keyCell.setPadding(5);
//        keyCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
//        table.addCell(keyCell);
//
//        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
//        valueCell.setPadding(5);
//        table.addCell(valueCell);
//    }
}