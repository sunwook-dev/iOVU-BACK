package com.iovu.iovuback.service;

import com.iovu.iovuback.domain.ReportList;        // Report → ReportList
import com.iovu.iovuback.domain.ReportDetail;
import com.iovu.iovuback.dto.report.ReportDetailDto;
import com.iovu.iovuback.exception.ReportNotFoundException;
import com.iovu.iovuback.mapper.ReportDetailMapper;
import com.iovu.iovuback.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper reportMapper;
    private final ReportDetailMapper detailMapper;

    /** 보고서 상세 (날짜·AI 필터) */
    @Transactional(readOnly = true)
    public ReportDetailDto detail(Long id, LocalDate date, String ai) {   // AiType → String

        // 1) 메타 조회
        ReportList meta = Optional.ofNullable(reportMapper.findById(id))
                .orElseThrow(() -> new ReportNotFoundException(id));

        // 2) 상세 스냅숏 조회
        ReportDetail det = Optional.ofNullable(
                detailMapper.findByReportDateAi(id, date, ai))
                .orElseThrow(() -> new ReportNotFoundException(id));

        // 3) DTO 변환 (snake_case getter 사용)
        return ReportDetailDto.from(meta, det);
    }

    /** 보고서 삭제 */
    public void delete(Long id) {
        if (reportMapper.deleteById(id) == 0)
            throw new ReportNotFoundException(id);
    }
}
