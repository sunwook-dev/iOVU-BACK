package com.iovu.oauth2.dto;

public class UserInfoDto {
    private String id;
    private String name;
    private String email;
    private String provider;
    private String avatar;

    public UserInfoDto() {}

    public UserInfoDto(String id, String name, String email, String provider, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.avatar = avatar;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
