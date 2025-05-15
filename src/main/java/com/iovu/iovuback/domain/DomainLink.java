package com.iovu.iovuback.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainLink {
    private Integer domain_link_id;
    private Integer report_detail_id;
    private String domain_name;
    private double rate;
    private Integer chatgpt;
    private Integer gemini;
    private String category;

    private List<LinkList> link_list;


    public DomainLink(Integer report_detail_id, String domain_name, double rate, Integer chatgpt, Integer gemini, String category, List<LinkList> link_list) {
        this.report_detail_id = report_detail_id;
        this.domain_name = domain_name;
        this.rate = rate;
        this.chatgpt = chatgpt;
        this.gemini = gemini;
        this.category = category;
        this.link_list = link_list;
    }

}