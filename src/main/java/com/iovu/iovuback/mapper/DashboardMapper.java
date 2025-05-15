package com.iovu.iovuback.mapper;

import com.iovu.iovuback.domain.Dashboard;
import com.iovu.iovuback.domain.DashboardDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DashboardMapper {

 public DashboardDTO getDashboardById(@Param("dashboard_id") Integer dashboard_id);
    public List<DashboardDTO> getDashboardDTOByReportDetailId(@Param("report_detail_id") Integer report_detail_id);

    public Dashboard getDashboard(@Param("reportId") Long reportId);

}
