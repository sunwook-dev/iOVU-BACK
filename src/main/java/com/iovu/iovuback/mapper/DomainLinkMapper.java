package com.iovu.iovuback.mapper;

import com.iovu.iovuback.domain.DomainLink;
import com.iovu.iovuback.domain.LinkList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DomainLinkMapper {
    public List<DomainLink> findDetail(Integer report_detail_id);
    public List<DomainLink> findDomainByReportId(Integer report_detail_id);
    public List<LinkList> findLinkListByReportId(Integer report_detail_id);
}
