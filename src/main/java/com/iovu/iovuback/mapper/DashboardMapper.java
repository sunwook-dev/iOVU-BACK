package com.iovu.iovuback.mapper;

import com.iovu.iovuback.domain.DashBoard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardMapper {
    List<DashBoard> getDashboard(@Param("aiType") String aiType);

}
