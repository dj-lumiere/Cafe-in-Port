package com.ssafy.cafe.model.dto;

public class OrderFrequency {
    private Integer pId;
    private Integer totalQuantity;

    // Constructors
    public OrderFrequency() {}

    public OrderFrequency(int pId, int totalQuantity) {
        this.pId = pId;
        this.totalQuantity = totalQuantity;
    }

    // Getters and Setters
    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}