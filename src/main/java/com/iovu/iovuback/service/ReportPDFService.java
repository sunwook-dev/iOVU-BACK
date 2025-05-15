package com.iovu.iovuback.service;

import com.iovu.iovuback.domain.*;
import com.iovu.iovuback.mapper.DomainLinkMapper;
import com.iovu.iovuback.mapper.ReportDetailMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Font;
//import com.itextpdf.text.BaseFont;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportPDFService {

    @Autowired
    private final DomainLinkService domainLinkService;
    @Autowired
    private ReportDetailMapper reportDetailMapper;
    @Autowired
    private DomainLinkMapper domainLinkMapper;
//    private final ResourceLoader resourceLoader;

    @Value("${pdf.font.path}")
    private String fontPath;


    /**
     * 도메인 링크 데이터를 PDF로 변환
     */
    public ByteArrayInputStream createDomainLinkPdf(Integer report_detail_id)
            throws DocumentException, IOException {
        // 데이터 조회
        List<DomainLink> domainLinks =
                domainLinkService.getDomainByReportDetail(report_detail_id);

        if (domainLinks.isEmpty()) {
            throw new RuntimeException("해당 키워드에 대한 보고서를 찾을 수 없습니다.");
        }

        // PDF 문서 생성
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // 페이지 이벤트 핸들러 추가 (머리글/바닥글)
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        // 바닥글 추가
                        PdfContentByte cb = writer.getDirectContent();
                        Font footerFont = getKoreanFont(8);

                        // 현재 날짜/시간
                        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        // 페이지 번호
                        String pageText = String.format("페이지 %d / %d", writer.getPageNumber(), writer.getPageNumber());

                        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase(dateTime, footerFont),
                                document.leftMargin(), document.bottomMargin() - 20, 0);

                        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                                new Phrase(pageText, footerFont),
                                document.right() - document.rightMargin(), document.bottomMargin() - 20, 0);
                    } catch (Exception e) {
                        log.error("PDF 페이지 이벤트 처리 중 오류 발생", e);
                    }
                }
            });

            document.open();

            // 1. 제목 추가
            addTitle(document, report_detail_id);

            // 2. 테이블 헤더 추가
            PdfPTable table = createTable();
            addTableHeader(table);

            // 3. 테이블 데이터 추가
            addTableData(table, domainLinks);

            document.add(table);

            // 4. 요약 정보 추가
            addSummary(document, domainLinks);

//            document.close();

        } catch (Exception e) {
            log.error("PDF 생성 중 오류 발생", e);
//            document.close();
            throw e;
        }finally {
            document.close();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * 테이블 생성
     */
    private PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(8); // 8개 컬럼
        table.setWidthPercentage(100);

        // 컬럼 너비 설정
        float[] columnWidths = {0.5f, 2f, 3f, 1f, 0.5f, 0.5f, 0.5f, 1f};
        table.setWidths(columnWidths);

        return table;
    }

    /**
     * 제목 추가
     */
    private void addTitle(Document document, Integer report_detail_id) throws DocumentException, IOException {
        Font titleFont = getKoreanFont(18, Font.BOLD);
        Font subtitleFont = getKoreanFont(12);

        Paragraph title = new Paragraph("도메인 및 링크분석", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        ReportDTO reportDTO = reportDetailMapper.getReportDetailWithKeyword(report_detail_id);
        String keyword = reportDTO != null && reportDTO.getKeyword() != null ? reportDTO.getKeyword() : "N/A";

        Paragraph subtitle = new Paragraph(
                "보고서 : " + keyword + " | 생성일시: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);
    }

    /**
     * 테이블 헤더 추가
     */
    private void addTableHeader(PdfPTable table) throws IOException, DocumentException  {
        // 헤더 셀 설정
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(new BaseColor(220, 220, 220));
        headerCell.setPadding(5);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font headerFont = getKoreanFont(11, Font.BOLD);
// 다른 한글 폰트 시도
//        BaseFont baseFont = BaseFont.createFont("font/NotoSansKR-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        Font headerFont = new Font(baseFont, 11, Font.BOLD);
        // 헤더 텍스트 배열
        String[] headers = {"번호", "도메인", "링크", "전체에서%", "총", "구글", "챗 gpt", "분류"};

        // 각 헤더 셀 추가
        for (String header : headers) {
            headerCell.setPhrase(new Phrase(header, headerFont));
            table.addCell(headerCell);
        }

        // 헤더 행이 각 페이지에서 반복되도록 설정
        table.setHeaderRows(1);
    }

    /**
     * 테이블 데이터 추가
     */
    private void addTableData(PdfPTable table, List<DomainLink> domainLinks) throws IOException, DocumentException  {
        // 기본 폰트 설정
        Font dataFont = getKoreanFont(10);
        Font linkFont = getKoreanFont(9);

        for (int i = 0; i < domainLinks.size(); i++) {
            DomainLink domain = domainLinks.get(i);

            // 상위 도메인(비율 5% 이상)에 대한 강조 스타일 설정
            boolean isHighlighted = domain.getRate() >= 5.0;
            BaseColor rowColor = isHighlighted ? new BaseColor(255, 245, 235) : null;

            // 1. 번호
            addCell(table, String.valueOf(i + 1), dataFont, Element.ALIGN_CENTER, rowColor);

            // 2. 도메인
            addCell(table, domain.getDomain_name(), dataFont, Element.ALIGN_LEFT, rowColor);

            // 3. 링크 (첫 번째 링크만 표시)
            String linkText = domain.getLink_list() != null && !domain.getLink_list().isEmpty()
                    ? domain.getLink_list().get(0).getLink()
                    : "";
            addCell(table, linkText, linkFont, Element.ALIGN_LEFT, rowColor);

            // 4. 전체에서 %
            addCell(table, formatPercent(domain.getRate()), dataFont, Element.ALIGN_CENTER, rowColor);

            // 5. 총 링크 수
            int totalLinks = domain.getLink_list() != null ? domain.getLink_list().size() : 0;
            addCell(table, String.valueOf(totalLinks), dataFont, Element.ALIGN_CENTER, rowColor);

            // 6. 구글 (gemini)
            addCell(table, String.valueOf(domain.getGemini()), dataFont, Element.ALIGN_CENTER, rowColor);

            // 7. 챗 gpt
            addCell(table, String.valueOf(domain.getChatgpt()), dataFont, Element.ALIGN_CENTER, rowColor);

            // 8. 분류
            addCell(table, domain.getCategory(), dataFont, Element.ALIGN_CENTER, rowColor);
        }
    }

    /**
     * 셀 추가 헬퍼 메소드 (배경색 지정 가능)
     */
    private void addCell(PdfPTable table, String text, Font font, int alignment, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);

        if (backgroundColor != null) {
            cell.setBackgroundColor(backgroundColor);
        }

        table.addCell(cell);
    }

    /**
     * 퍼센트 포맷팅
     */
    private String formatPercent(double value) {
        return String.format("%.1f%%", value);
    }

    /**
     * 요약 정보 추가
     */
    private void addSummary(Document document, List<DomainLink> domainLinks) throws DocumentException, IOException {
        document.add(new Paragraph("\n"));

        Font titleFont = getKoreanFont(14, Font.BOLD);
        Font contentFont = getKoreanFont(11);

        Paragraph summaryTitle = new Paragraph("요약 정보", titleFont);
        summaryTitle.setSpacingBefore(15);
        summaryTitle.setSpacingAfter(10);
        document.add(summaryTitle);

        // 카테고리별 통계
        document.add(new Paragraph("● 카테고리별 도메인 수:", contentFont));

        // 카테고리별 도메인 수 계산
        Map<String, Long> categoryStats = domainLinks.stream()
                .collect(Collectors.groupingBy(DomainLink::getCategory, Collectors.counting()));

        for (Map.Entry<String, Long> entry : categoryStats.entrySet()) {
            document.add(new Paragraph(String.format("   - %s: %d개", entry.getKey(), entry.getValue()), contentFont));
        }

        document.add(new Paragraph("\n"));

        // 상위 3개 도메인 정보
        document.add(new Paragraph("● 상위 3개 도메인:", contentFont));

        List<DomainLink> top3Domains = domainLinks.stream()
                .sorted((d1, d2) -> Double.compare(d2.getRate(), d1.getRate()))
                .limit(3)
                .collect(Collectors.toList());

        for (int i = 0; i < top3Domains.size(); i++) {
            DomainLink domain = top3Domains.get(i);
            document.add(new Paragraph(
                    String.format("   %d. %s (%.1f%%) - 링크 %d개",
                            i + 1, domain.getDomain_name(), domain.getRate(),
                            domain.getLink_list() != null ? domain.getLink_list().size() : 0),
                    contentFont
            ));
        }

        // AI 분석 결과 요약
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("● AI 분석 결과:", contentFont));

        int totalChatgpt = domainLinks.stream().mapToInt(DomainLink::getChatgpt).sum();
        int totalGemini = domainLinks.stream().mapToInt(DomainLink::getGemini).sum();

        document.add(new Paragraph(String.format("   - ChatGPT 참조 총 횟수: %d회", totalChatgpt), contentFont));
        document.add(new Paragraph(String.format("   - Google Gemini 참조 총 횟수: %d회", totalGemini), contentFont));

        String aiComparison = totalChatgpt > totalGemini ? "ChatGPT가 더 많이 참조됨" :
                totalChatgpt < totalGemini ? "Google Gemini가 더 많이 참조됨" :
                        "ChatGPT와 Google Gemini가 동일하게 참조됨";

        document.add(new Paragraph("   - AI 비교 결과: " + aiComparison, contentFont));
    }

    /**
     * 한글 폰트 가져오기
     */
    private Font getKoreanFont(int size) throws IOException, DocumentException {
        return getKoreanFont(size, Font.NORMAL);
    }

    /**
     * 한글 폰트 가져오기 (스타일 지정)
     */
//    private Font getKoreanFont(int size, int style) throws IOException, DocumentException {
//        Resource fontResource = resourceLoader.getResource(fontPath);
//        BaseFont baseFont = BaseFont.createFont(
//                fontResource.getFile().getAbsolutePath(),
//                BaseFont.IDENTITY_H,
//                BaseFont.EMBEDDED
//        );
//        return new Font(baseFont, size, style);
//    }
    private Font getKoreanFont(int size, int style) throws IOException, DocumentException {
        try {
            // 클래스패스에서 폰트 파일을 로드
            InputStream fontStream = getClass().getResourceAsStream("/font/NanumGothic.ttf");
            if (fontStream == null) {
                log.warn("폰트 파일을 찾을 수 없습니다. 기본 폰트를 사용합니다.");
                return new Font(Font.FontFamily.HELVETICA, size, style);
            }

            // 폰트 데이터를 바이트 배열로 읽기
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = fontStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] fontData = buffer.toByteArray();
            fontStream.close();

            // 바이트 배열에서 폰트 생성
            BaseFont baseFont = BaseFont.createFont("NanumGothic.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    false,
                    fontData,
                    null);
            return new Font(baseFont, size, style);
        } catch (Exception e) {
            log.warn("한글 폰트 로드 실패, 기본 폰트 사용: " + e.getMessage());
            return new Font(Font.FontFamily.HELVETICA, size, style);
        }
    }

    //리포트 pdf 내보내기
    public ByteArrayInputStream generateReportPdf(Integer report_detail_id)
            throws IOException, DocumentException {
        ReportDTO report = reportDetailMapper.getReportDetailWithKeyword(report_detail_id);

        if (report == null) {
            throw new RuntimeException("데이터가 없습니다.");
        }

        // 기존에 작성해둔 createDomainLinkPdf(...) 메서드 활용
        return createDomainLinkPdf(report_detail_id);
    }



}