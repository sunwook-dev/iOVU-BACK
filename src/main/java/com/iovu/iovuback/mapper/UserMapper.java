package com.iovu.iovuback.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    Integer selectUserIdByProviderAndSocialId(@Param("socialProvider") String socialProvider, @Param("socialId") String socialId);
    Integer selectReportIdByUserIdAndTitle(@Param("userId") Integer userId, @Param("reportTitle") String reportTitle);
}
