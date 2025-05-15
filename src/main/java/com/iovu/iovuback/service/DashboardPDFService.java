package com.iovu.iovuback.service;

import com.iovu.iovuback.domain.Dashboard;
import com.iovu.iovuback.domain.DashboardDTO;
import com.iovu.iovuback.mapper.DashboardMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Service
@Log4j2
@Slf4j
public class DashboardPDFService {

    @Autowired
    private DashboardMapper dashboardDTOMapper;

    @Value("${pdf.font.path}")
    private String fontPath;


    //    대시보드
    public ByteArrayInputStream generateDashboardPdfByKeyword(Integer report_detail_id) throws IOException, DocumentException {
        try {
            // 키워드로 모든 AI 유형의 대시보드 데이터 조회
            DashboardDTO dashboardDTO = dashboardDTOMapper.getDashboardById(report_detail_id);
            List<DashboardDTO> dashboards = dashboardDTOMapper.getDashboardDTOByReportDetailId(report_detail_id);
            String keyword = dashboardDTO != null && dashboardDTO.getKeyword() != null ? dashboardDTO.getKeyword() : "N/A";

            if (dashboards == null || dashboards.isEmpty()) {
                throw new RuntimeException("키워드 '" + keyword + "'에 대한 대시보드 데이터가 없습니다.");
            }

            log.info("대시보드 데이터 조회 성공: {} 개", dashboards.size());

            // PDF 생성
            return createDashboardPdf(keyword, dashboards);

        } catch (Exception e) {
            log.error("대시보드 PDF 생성 중 오류 발생: ", e);
            throw e;
        }
    }

    /**
     * 단일 Report Detail ID에 대한 대시보드 PDF 생성
     */
    public ByteArrayInputStream generateSingleDashboardPdf(Integer report_detail_id) throws IOException, DocumentException {
        try {
            // 특정 report_detail_id에 대한 대시보드 데이터 조회
            DashboardDTO dashboard = dashboardDTOMapper.getDashboardById(report_detail_id);

            if (dashboard == null) {
                throw new RuntimeException("보고서 ID " + report_detail_id + "에 대한 대시보드 데이터가 없습니다.");
            }

            log.info("단일 대시보드 데이터 조회 성공: {}", dashboard);

            // 리스트로 변환하여 PDF 생성 (단일 항목)
            List<DashboardDTO> dashboards = new List<DashboardDTO>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean contains(Object o) {
                    return false;
                }

                @Override
                public Iterator<DashboardDTO> iterator() {
                    return null;
                }

                @Override
                public Object[] toArray() {
                    return new Object[0];
                }

                @Override
                public <T> T[] toArray(T[] a) {
                    return null;
                }

                @Override
                public boolean add(DashboardDTO dashboardDTO) {
                    return false;
                }

                @Override
                public boolean remove(Object o) {
                    return false;
                }

                @Override
                public boolean containsAll(Collection<?> c) {
                    return false;
                }

                @Override
                public boolean addAll(Collection<? extends DashboardDTO> c) {
                    return false;
                }

                @Override
                public boolean addAll(int index, Collection<? extends DashboardDTO> c) {
                    return false;
                }

                @Override
                public boolean removeAll(Collection<?> c) {
                    return false;
                }

                @Override
                public boolean retainAll(Collection<?> c) {
                    return false;
                }

                @Override
                public void clear() {

                }

                @Override
                public DashboardDTO get(int index) {
                    return null;
                }

                @Override
                public DashboardDTO set(int index, DashboardDTO element) {
                    return null;
                }

                @Override
                public void add(int index, DashboardDTO element) {

                }

                @Override
                public DashboardDTO remove(int index) {
                    return null;
                }

                @Override
                public int indexOf(Object o) {
                    return 0;
                }

                @Override
                public int lastIndexOf(Object o) {
                    return 0;
                }

                @Override
                public ListIterator<DashboardDTO> listIterator() {
                    return null;
                }

                @Override
                public ListIterator<DashboardDTO> listIterator(int index) {
                    return null;
                }

                @Override
                public List<DashboardDTO> subList(int fromIndex, int toIndex) {
                    return List.of();
                }
            };
            dashboards.add(dashboard);

            // PDF 생성
            return createDashboardPdf(dashboard.getKeyword(), dashboards);

        } catch (Exception e) {
            log.error("단일 대시보드 PDF 생성 중 오류 발생: ", e);
            throw e;
        }
    }

    /**
     * 특정 report_detail_id에 대해 같은 report의 모든 AI 유형 대시보드 PDF 생성
     */
    public ByteArrayInputStream generateAllAIDashboardPdf(Integer report_detail_id) throws IOException, DocumentException {
        try {
            // report_detail_id로 해당 report의 모든 AI 유형 대시보드 조회
            List<DashboardDTO> dashboards = dashboardDTOMapper.getDashboardDTOByReportDetailId(report_detail_id);

            if (dashboards == null || dashboards.isEmpty()) {
                log.warn("report_detail_id {}에 대한 관련 대시보드 데이터가 없습니다.", report_detail_id);
                throw new RuntimeException("보고서 ID " + report_detail_id + "에 대한 관련 대시보드 데이터가 없습니다.");
            }

            log.info("report_detail_id {} 관련 대시보드 데이터 조회 성공: {} 개", report_detail_id, dashboards.size());

            // 첫 번째 대시보드에서 키워드 가져오기
            String keyword = dashboards.get(0).getKeyword();

            // PDF 생성
            return createDashboardPdf(keyword, dashboards);

        } catch (Exception e) {
            log.error("대시보드 PDF 생성 중 오류 발생: ", e);
            throw e;
        }
    }

    /**
     * 대시보드 데이터로 PDF 생성
     */
    private ByteArrayInputStream createDashboardPdf(String keyword, List<DashboardDTO> dashboards)
            throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // 페이지 이벤트 핸들러 설정
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        // 바닥글 추가
                        PdfContentByte cb = writer.getDirectContent();
                        Font footerFont = getKoreanFont(8);

                        // 현재 날짜/시간
                        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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
            addDashboardTitle(document, keyword);

            // 2. 각 AI 별 대시보드 추가
            for (DashboardDTO dashboard : dashboards) {
                addDashboardSection(document, dashboard);
            }

        } finally {
            document.close();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * 대시보드 제목 추가
     */
    private void addDashboardTitle(Document document, String keyword) throws DocumentException, IOException {
        Font titleFont = getKoreanFont(18, Font.BOLD);
        Font subtitleFont = getKoreanFont(12);

        // 한글 상수 정의
        final String DASHBOARD_TEXT = "대시보드";
        final String KEYWORD_TEXT = "키워드";
        final String CREATED_AT_TEXT = "생성일시";

        // 제목
        Paragraph titlePara = new Paragraph(DASHBOARD_TEXT, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(10);
        document.add(titlePara);

        // 부제목 (키워드)
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Paragraph subtitlePara = new Paragraph(
                KEYWORD_TEXT + ": " + keyword + " | " + CREATED_AT_TEXT + ": " + createdAt,
                subtitleFont);
        subtitlePara.setAlignment(Element.ALIGN_CENTER);
        subtitlePara.setSpacingAfter(20);
        document.add(subtitlePara);
    }

    /**
     * AI 별 대시보드 섹션 추가
     */
    private void addDashboardSection(Document document, DashboardDTO dashboard) throws DocumentException, IOException {
        // 한글 상수 정의
        final String USAGE_COUNT_TEXT = "참여 횟수";
        final String BRAND_COVERAGE_TEXT = "브랜드 커버리지";
        final String DOMAIN_COVERAGE_TEXT = "도메인 커버리지";
        final String BRAND_RANK_TEXT = "브랜드 순위";
        final String BRAND_MENTION_TEXT = "브랜드 언급 횟수";

        // AI 유형 제목
        Font sectionTitleFont = getKoreanFont(14, Font.BOLD);
        Font contentFont = getKoreanFont(11);

        // AI 유형 표시 (ChatGPT, Gemini, Cloud 등)
        Paragraph aiTypePara = new Paragraph(dashboard.getAi_type_name(), sectionTitleFont);
        aiTypePara.setSpacingBefore(15);
        aiTypePara.setSpacingAfter(10);
        document.add(aiTypePara);

        // 2x3 그리드로 대시보드 항목 표시
        PdfPTable grid = new PdfPTable(2); // 2개 컬럼
        grid.setWidthPercentage(100);
        grid.setSpacingBefore(10);
        grid.setSpacingAfter(20);

        // 1. 참여 횟수
        PdfPCell cell1 = createDashboardCell(USAGE_COUNT_TEXT, String.valueOf(dashboard.getResult_summary()), contentFont);
        grid.addCell(cell1);

        // 2. 브랜드 커버리지
        String brandCoverage = String.format("%.1f%% (%d)", dashboard.getBrand_rate1(), dashboard.getBrand_rate2());
        PdfPCell cell2 = createDashboardCell(BRAND_COVERAGE_TEXT, brandCoverage, contentFont);
        grid.addCell(cell2);

        // 3. 브랜드 순위
        PdfPCell cell3 = createDashboardCell(BRAND_RANK_TEXT, String.valueOf(dashboard.getBrand_rank()), contentFont);
        grid.addCell(cell3);

        // 4. 도메인 커버리지
        String domainCoverage = String.format("%.1f%% (%d)", dashboard.getDomain_rate1(), dashboard.getDomain_rate2());
        PdfPCell cell4 = createDashboardCell(DOMAIN_COVERAGE_TEXT, domainCoverage, contentFont);
        grid.addCell(cell4);

        // 5. 브랜드 언급 횟수
        PdfPCell cell5 = createDashboardCell(BRAND_MENTION_TEXT, String.valueOf(dashboard.getBrand_mention()), contentFont);
        grid.addCell(cell5);

        // 6. 빈 셀
        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBorder(Rectangle.NO_BORDER);
        grid.addCell(emptyCell);

        document.add(grid);

        // 구분선 추가
        LineSeparator line = new LineSeparator();
        line.setPercentage(90);
        line.setLineColor(new BaseColor(200, 200, 200));
        document.add(new Chunk(line));
    }

    /**
     * 대시보드 항목 셀 생성
     */
    private PdfPCell createDashboardCell(String label, String value, Font font) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBorder(Rectangle.NO_BORDER);

        // 셀 내부에 항목 제목과 값을 표시하는 테이블
        PdfPTable innerTable = new PdfPTable(1);
        innerTable.setWidthPercentage(100);

        // 항목 값 (큰 숫자)
        Font valueFont = getKoreanFont(24, Font.BOLD);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        innerTable.addCell(valueCell);

        // 항목 레이블
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingTop(5);
        innerTable.addCell(labelCell);

        cell.addElement(innerTable);
        return cell;
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
    private Font getKoreanFont(int size, int style) throws IOException, DocumentException {
        try {
            log.info("한글 폰트 로딩 시도...");

            // 내장된 HYGoThic 폰트 사용 (한글 지원)
            BaseFont baseFont = BaseFont.createFont(
                    "HYGoThic-Medium",
                    "UniKS-UCS2-H",
                    BaseFont.NOT_EMBEDDED
            );

            log.info("한글 폰트 로딩 성공");
            return new Font(baseFont, size, style);
        } catch (Exception e) {
            log.error("한글 폰트 로드 실패: ", e);

            // 대체 방법으로 NanumGothic 폰트 시도
            try {
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
                BaseFont customBaseFont = BaseFont.createFont("NanumGothic.ttf",
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED,
                        false,
                        fontData,
                        null);
                return new Font(customBaseFont, size, style);
            } catch (Exception ex) {
                log.error("대체 폰트 로드도 실패: ", ex);
                return new Font(Font.FontFamily.HELVETICA, size, style);
            }
        }
    }
}

