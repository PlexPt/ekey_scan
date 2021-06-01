package com.repair.repair.dto;

import com.repair.repair.App;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AdminOp {

    String id;

    String username;

    String content;

    public static AdminOp getInstance(String content) {
        AdminOp op = new AdminOp().setId(App.appData.user.getMobile())
                .setUsername(App.appData.user.getName())
                .setContent(content);
        return op;
    }
}
