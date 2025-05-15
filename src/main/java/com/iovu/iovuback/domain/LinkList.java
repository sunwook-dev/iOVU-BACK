package com.iovu.iovuback.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkList {
    private Integer link_id;
    private Integer domain_link_id;
    private String link;

    public LinkList(Integer domain_link_id, String link) {
        this.domain_link_id = domain_link_id;
        this.link = link;
    }

}