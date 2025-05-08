package com.iovu.iovuback.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
    private Integer id;
    private String name;
    private String address;
    private Timestamp created_at;
    private Timestamp updated_at;

    public Restaurant(String name, String address, Timestamp created_at, Timestamp updated_at) {
        this.name = name;
        this.address = address;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

}
