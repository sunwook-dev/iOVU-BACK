package com.iovu.iovuback.service;

import com.iovu.iovuback.domain.ReportEntity;
import com.iovu.iovuback.dto.ReportSummaryDTO;
import com.iovu.iovuback.mapper.ReportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    private final ReportMapper reportMapper;

    public ReportService(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    public List<ReportSummaryDTO> getReportSummariesByUser(Integer userUid) {
        return reportMapper.selectReportSummariesByUserUid(userUid);
    }

    public ReportEntity getReportDetail(Integer reportId) {
        return reportMapper.selectReportById(reportId);
    }

    @Transactional
    public int registerReport(ReportEntity report) {
        report.setCreatedAt(new Date());
        return reportMapper.insertReport(report);
    }
}
