package com.ssafy.cafe.model.dto;

import com.ssafy.cafe.enumclasses.DrinkSize;
import com.ssafy.cafe.enumclasses.ProductType;
import com.ssafy.cafe.enumclasses.Temperature;

public class ProductWithDetail extends Product {
    Integer detailId;
    DrinkSize size;
    Integer price;
    Temperature temperature;

    public ProductWithDetail(Integer id, String name, ProductType type, String img, Integer detailId, DrinkSize size, Integer price, Temperature temperature) {
        super(id, name, type, img);
        this.detailId = detailId;
        this.size = size;
        this.price = price;
        this.temperature = temperature;
    }

    public ProductWithDetail(Integer id, String name, ProductType type, String img, DrinkSize size, Integer price, Temperature temperature) {
        super(id, name, type, img);
        this.size = size;
        this.price = price;
        this.temperature = temperature;
    }

    public ProductWithDetail(Integer id, DrinkSize size, Temperature temperature) {
        super.setId(id);
        this.size = size;
        this.temperature = temperature;
    }

    public ProductWithDetail(Integer p_id, String name, ProductType type, String img) {
        super(p_id, name, type, img);
    }

    public ProductWithDetail(String name, ProductType type, String img) {
        super(name, type, img);
    }

    public ProductWithDetail() {
    }

    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    public DrinkSize getSize() {
        return size;
    }

    public void setSize(DrinkSize size) {
        this.size = size;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "ProductWithDetail{" + "detailId=" + detailId + ", size=" + size + ", price=" + price + ", isHot=" + temperature + "} " + super.toString();
    }
}
