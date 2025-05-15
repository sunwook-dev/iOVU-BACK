package com.iovu.iovuback.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDetail {
    private Integer report_detail_id;
    private Integer report_id;
    private String ai_type;
    private Timestamp created_at;
    private String json_data;

    private Consulting consulting;
    private DomainLink domain_link;
    private Dashboard dashboard;


    public ReportDetail(Integer report_id, String ai_type, Timestamp created_at, String json_data, Consulting consulting) {
        this.report_id = report_id;
        this.ai_type = ai_type;
        this.created_at = created_at;
        this.json_data = json_data;
        this.consulting = consulting;

    }

}