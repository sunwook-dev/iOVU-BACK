package com.iovu.iovuback.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    // ReportDetail 관련 필드
    private Integer report_detail_id;
    private Integer report_id;
    private String ai_type;
    private Timestamp created_at;
    private String json_data;

    // ReportList에서 가져올 필드
    private String keyword;
    private Integer data_volume;
    // 필요에 따라 Consulting, DomainLink 등의 객체 추가 가능
}
