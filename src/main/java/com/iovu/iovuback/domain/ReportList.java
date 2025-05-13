package com.iovu.iovuback.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportList {
    private Integer report_id;
    private String keyword;
    private String status;
    private Integer data_volume;
    private Timestamp created_at;
    private Timestamp updated_at;

    private List<ReportDetail> report_details;


    public ReportList(String keyword, String status, Integer data_volume, List<ReportDetail> report_details) {
        this.keyword = keyword;
        this.status = status;
        this.data_volume = data_volume;
        this.report_details = report_details;
    }

}