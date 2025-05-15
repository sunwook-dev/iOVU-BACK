package com.iovu.iovuback.service;

import com.iovu.iovuback.domain.DomainLink;
import com.iovu.iovuback.domain.LinkList;
import com.iovu.iovuback.mapper.DomainLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class DomainLinkService {
    @Autowired
    public DomainLinkMapper mapper;

    public List<DomainLink> getDomainBYDetail(Integer report_detail_id) {
        return mapper.findDetail(report_detail_id);
    }

    public List<DomainLink> getDomainByReportDetail(Integer report_detail_id) {
        return mapper.findDomainByReportId(report_detail_id);
    }

    public List<LinkList> getLinkByReportDetail(Integer report_detail_id) {
        return mapper.findLinkListByReportId(report_detail_id);
    }
}
