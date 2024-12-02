package com.ssafy.cafe.model.dto;

public class OrderDetail extends Order {
    private Integer id;
    private Integer orderId;
    private Integer productDetailId;
    private Integer quantity;

    public OrderDetail(Integer id, Integer orderId, Integer productId, Integer quantity) {
        super();
        this.id = id;
        this.orderId = orderId;
        this.productDetailId = productId;
        this.quantity = quantity;
    }

    public OrderDetail(Integer productId, Integer quantity) {
        this.productDetailId = productId;
        this.quantity = quantity;
    }

    public OrderDetail(Integer orderId, Integer productId, Integer quantity) {
        this.orderId = orderId;
        this.productDetailId = productId;
        this.quantity = quantity;
    }

    public OrderDetail() {}

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

	public Integer getProductDetailId() {
		return productDetailId;
	}

	public void setProductDetailId(Integer productDetailId) {
		this.productDetailId = productDetailId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "OrderDetail [id=" + id + ", orderId=" + orderId + ", productDetailId=" + productDetailId + ", quantity=" + quantity
				+ "]";
	}


}
