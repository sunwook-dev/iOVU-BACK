package com.iovu.oauth2.mapper;

import com.iovu.oauth2.model.AuthData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthDataMapper {
    @Insert("INSERT INTO auth_data (access_token, refresh_token, user_info, oauth_provider) VALUES (#{accessToken}, #{refreshToken}, #{userInfo}, #{oauthProvider})")
    void insert(AuthData authData);
}
