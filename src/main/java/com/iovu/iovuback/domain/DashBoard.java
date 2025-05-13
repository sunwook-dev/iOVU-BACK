package com.iovu.iovuback.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashBoard {
    private Integer dashboard_id;
    private Integer report_detail_id;
    private Integer result_summary;
    private Double brand_rate1;
    private Integer brand_rate2;
    private Integer brand_rank;
    private Double domain_rate1;
    private Integer domain_rate2;
    private Integer brand_mention;
}


