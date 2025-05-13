package com.iovu.iovuback.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dashboard {
    private Integer dashboard_id;
    private Integer report_detail_id;
    private Integer result_summary;
    private double brand_rate1;
    private Integer brand_rate2;
    private Integer brand_rank;
    private double domain_rate1;
    private Integer domain_rate2;
    private Integer brand_mention;


    public Dashboard(Integer report_detail_id, Integer result_summary, double brand_rate1, Integer brand_rate2, Integer brand_rank, double domain_rate1, Integer domain_rate2, Integer brand_mention) {
        this.report_detail_id = report_detail_id;
        this.result_summary = result_summary;
        this.brand_rate1 = brand_rate1;
        this.brand_rate2 = brand_rate2;
        this.brand_rank = brand_rank;
        this.domain_rate1 = domain_rate1;
        this.domain_rate2 = domain_rate2;
        this.brand_mention = brand_mention;
    }


}