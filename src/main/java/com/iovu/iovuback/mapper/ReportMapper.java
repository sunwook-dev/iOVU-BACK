package com.iovu.iovuback.mapper;

import com.iovu.iovuback.domain.ReportEntity;
import com.iovu.iovuback.dto.ReportSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReportMapper {
    List<ReportSummaryDTO> selectReportSummariesByUserUid(Integer userUid); // userUid는 user_id
    ReportEntity selectReportById(Integer reportId);
    int insertReport(ReportEntity report);
}
