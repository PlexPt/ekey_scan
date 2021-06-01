package com.repair.repair.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import lombok.Data;

@Data
public class Order {

    long id;

    String name;

    String des;

    /**
     * 下单人
     */
    UserDTO user;

    /**
     * 接单人
     */
    UserDTO worker;

    /**
     * 下单时间
     */
    Date orderTime;
    /**
     * 签到时间
     */
    Date checkinTime;

    /**
     * 签退时间
     */
    Date checkoutTime;

    /**
     * 待接单 已接单 已签到 已签退 待支付  待评价 已完成 已取消
     */
    String status = "待接单";

    /**
     * 费用
     */
    BigDecimal amount = BigDecimal.ZERO;


    /**
     * 评价
     */
    String remark;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
