package com.iovu.iovuback.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportEntity {
    private Integer reportId; // report_id
    private Integer userId; // user_id (FK)
    private String reportTitle; // report_title
    private String reportContent; // report_content
    private Date createdAt; // created_at
}
