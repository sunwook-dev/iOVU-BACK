package com.iovu.iovuback.dto.report;

import com.iovu.iovuback.domain.ReportDetail;
import com.iovu.iovuback.domain.ReportList;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** GET /reports/{id} 응답 모델 */
public record ReportDetailDto(
        Long id,
        String keyword,
        String status,
        Integer dataVolume,
        String ai,                 // enum 대신 문자열
        LocalDate snapshotDate,    // 스냅숏 기준일
        String snapshotText,       // CLOB → String
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /** ReportList + ReportDetail → DTO 변환 */
    public static ReportDetailDto from(ReportList meta, ReportDetail det) {
        return new ReportDetailDto(
                meta.getReport_id().longValue(),
                meta.getKeyword(),
                meta.getStatus(),
                meta.getData_volume(),
                det.getAi_type(),
                det.getCreated_at().toLocalDateTime().toLocalDate(),
                det.getJson_data(),
                meta.getCreated_at().toLocalDateTime(),
                meta.getUpdated_at() == null ? null : meta.getUpdated_at().toLocalDateTime()
        );
    }
}
