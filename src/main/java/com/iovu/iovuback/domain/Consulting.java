package com.iovu.iovuback.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consulting {
    private Integer consulting_id;
    private Integer report_detail_id;
    private String result;
    private String consulting;

    public Consulting(Integer report_detail_id, String result, String consulting) {
        this.report_detail_id = report_detail_id;
        this.result = result;
        this.consulting = consulting;
    }

}