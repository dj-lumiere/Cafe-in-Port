package com.ssafy.cafe.model.dto;

import com.ssafy.cafe.enumclasses.ProductType;

public class Product {
    private Integer id;
    private String name;
    private ProductType type;
    private String img;

    public Product(Integer id, String name, ProductType type, String img) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.img = img;
    }

    public Product(String name, ProductType type, String img) {
        this.name = name;
        this.type = type;
        this.img = img;
    }
    public Product() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", type=" + type + ", img=" + img + "]";
	}




}
