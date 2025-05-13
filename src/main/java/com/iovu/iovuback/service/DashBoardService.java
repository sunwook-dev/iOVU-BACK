package com.iovu.iovuback.service;

import com.iovu.iovuback.domain.DashBoard;

import java.util.List;

public interface DashBoardService {
    List<DashBoard> getDashboard(String aiType);

}
