package com.iovu.iovuback.mapper;

import com.iovu.iovuback.domain.ReportDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReportDetailMapper {

    /** 단일 스냅숏 조회 (날짜·AI 필터) */
    ReportDetail findByReportDateAi(@Param("reportId") Long      reportId,
                                    @Param("date")     LocalDate date,     // null → 전체
                                    @Param("ai")       String    ai);      // null → 전체

    /** 특정 보고서의 모든 스냅숏 목록(최신순) */
    List<ReportDetail> findAllByReport(@Param("reportId") Long reportId);

    /** INSERT – 시퀀스 report_detail_id_seq 사용 */
    int insertDetail(ReportDetail detail);
}
