package com.ssafy.cafe.model.dto;

import com.ssafy.cafe.enumclasses.OrderStatus;

import java.util.Date;
import java.util.List;

public class OrderInfo {
    private Integer id;
    private Integer userNo;
    private String orderTable;
    private Date orderTime;
    private OrderStatus status;
    private List<OrderDetailInfo> details;


    public OrderInfo(Integer id, Integer userNo, String orderTable, Date orderTime, OrderStatus status) {
        this.id = id;
        this.userNo = userNo;
        this.orderTable = orderTable;
        this.orderTime = orderTime;
        this.status = status;
    }

    public OrderInfo(Integer userNo, String orderTable, Date orderTime, OrderStatus status) {
        this.userNo = userNo;
        this.orderTable = orderTable;
        this.orderTime = orderTime;
        this.status = status;
    }

    public OrderInfo() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    public String getOrderTable() {
        return orderTable;
    }

    public void setOrderTable(String orderTable) {
        this.orderTable = orderTable;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderDetailInfo> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailInfo> details) {
        this.details = details;
    }

}
