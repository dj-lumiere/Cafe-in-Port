package com.ssafy.cafe.model.dto;

public class Stamp {
    private Integer id;
    private Integer userNo;
    private Integer orderId;
    private Integer quantity;

    public Stamp(Integer id, Integer userNo, Integer orderId, Integer quantity) {
        this.id = id;
        this.userNo = userNo;
        this.orderId = orderId;
        this.quantity = quantity;
    }

    public Stamp(Integer userNo, Integer orderId, Integer quantity) {
        this.userNo = userNo;
        this.orderId = orderId;
        this.quantity = quantity;
    }

    public Stamp() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    @Override
    public String toString() {
        return "Stamp{" + "id=" + id + ", userNo=" + userNo + ", orderId=" + orderId + ", quantity=" + quantity + '}';
    }
}

