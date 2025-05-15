package com.iovu.iovuback.mapper;

import com.iovu.iovuback.domain.ReportList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportMapper {

    ReportList findById(@Param("id") Long id);

    int deleteById(@Param("id") Long id);

    // TODO: insertReport, existsByKeyword, findPage, countPage … 필요 시 추가
}
