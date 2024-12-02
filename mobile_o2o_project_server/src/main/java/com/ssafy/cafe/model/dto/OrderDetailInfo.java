package com.ssafy.cafe.model.dto;

import com.ssafy.cafe.enumclasses.DrinkSize;
import com.ssafy.cafe.enumclasses.ProductType;
import com.ssafy.cafe.enumclasses.Temperature;

import java.util.Date;

public class OrderDetailInfo {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Date date;
    private String img; // 상품이미지
    private String name;// 상품명
    private ProductType type; // 상품 종류
    private DrinkSize size;
    private Temperature temperature;
    private Integer unitPrice; //상품가격
    private Integer sumPrice; // quantity * 상품가격

    public OrderDetailInfo(Integer id, Integer orderId, Integer productId, Integer quantity, Date date, String img, String name, ProductType type, DrinkSize size, Temperature temperature, Integer unitPrice, Integer sumPrice) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.date = date;
        this.img = img;
        this.name = name;
        this.type = type;
        this.size = size;
        this.temperature = temperature;
        this.unitPrice = unitPrice;
        this.sumPrice = sumPrice;
    }

    public OrderDetailInfo(Integer orderId, Integer productId, Integer quantity, String img, String name, ProductType type, DrinkSize size, Temperature temperature, Integer unitPrice, Integer sumPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.img = img;
        this.name = name;
        this.type = type;
        this.size = size;
        this.temperature = temperature;
        this.unitPrice = unitPrice;
        this.sumPrice = sumPrice;
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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Integer sumPrice) {
        this.sumPrice = sumPrice;
    }

    public DrinkSize getSize() {
        return size;
    }

    public void setSize(DrinkSize size) {
        this.size = size;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
