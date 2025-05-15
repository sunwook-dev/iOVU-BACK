package com.iovu.iovuback.mapper;


import com.iovu.iovuback.domain.ReportDTO;
import com.iovu.iovuback.domain.ReportDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportDetailMapper {

    public ReportDetail getReportDetailById(@Param("report_detail_id") Integer report_detail_id);
    public ReportDTO getReportDetailWithKeyword(@Param("report_detail_id") Integer report_detail_id);
//    public ReportDetail getReportDetailsByReportId(@Param("report_id") Integer report_detail_name);
}
