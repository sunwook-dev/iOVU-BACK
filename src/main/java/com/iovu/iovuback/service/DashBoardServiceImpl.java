package com.iovu.iovuback.service;

import com.iovu.iovuback.domain.DashBoard;
import com.iovu.iovuback.mapper.DashboardMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService{
    private final DashboardMapper dashboardMapper;

    @Override
    public List<DashBoard> getDashboard(String aiType) {
        return dashboardMapper.getDashboard(aiType);
    }
}
