package com.iovu.iovuback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDTO {
    private Integer reportId;
    private String reportTitle;
}
