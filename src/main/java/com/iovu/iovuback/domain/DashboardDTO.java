package com.iovu.iovuback.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {
    private Integer dashboard_id;
    private Integer report_detail_id;
    private Integer result_summary;
    private double brand_rate1;
    private Integer brand_rate2;
    private Integer brand_rank;
    private double domain_rate1;
    private Integer domain_rate2;
    private Integer brand_mention;

    // ReportDTO에서 가져올 필드
    private String keyword;
    private String ai_type;

    // 화면에 표시할 AI 유형 이름(ChatGPT, Gemini, Cloud 등)
    private String ai_type_name;
}