package com.iovu.iovuback.mapper;

import com.iovu.iovuback.domain.Restaurant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestMapper {
    public List<Restaurant> selectAll();
}
