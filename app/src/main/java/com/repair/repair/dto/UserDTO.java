package com.repair.repair.dto;

import lombok.Data;

@Data
public class UserDTO {


    String mobile;

    String name;

    String password;

    /**
     * 0 用户 1 管理员
     */
    int type = 0;

//    String address;

}
