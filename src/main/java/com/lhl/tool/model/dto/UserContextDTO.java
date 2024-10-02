package com.lhl.tool.model.dto;

import lombok.Data;

@Data
public class UserContextDTO {
    private Long userId;

    private String username;

    private Boolean isAdmin;

    private Long shopId;

    private String shopName;
}
