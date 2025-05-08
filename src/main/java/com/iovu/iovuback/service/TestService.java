package com.iovu.iovuback.service;

import com.iovu.iovuback.domain.Restaurant;
import com.iovu.iovuback.mapper.TestMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {
    private TestMapper restaurantMapper;

    public TestService(TestMapper restaurantMapper) {
        this.restaurantMapper = restaurantMapper;
    }

    public List<Restaurant> selectAll(){
        return restaurantMapper.selectAll();
    }
}
