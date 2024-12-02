package com.ssafy.cafe_in_port_client.data.model.response

import com.google.gson.annotations.SerializedName
import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.ProductType
import com.ssafy.cafe_in_port_client.common.enums.Temperature
import java.util.Date

data class OrderDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("orderId") val orderId: Int,
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("img") val productImg: String,
    @SerializedName("name") val productName: String,
    @SerializedName("type") val productType: ProductType,
    @SerializedName("size") val size: DrinkSize,
    @SerializedName("temperature") val temperature: Temperature,
    @SerializedName("unitPrice") val unitPrice: Int,
    @SerializedName("sumPrice") val sumPrice: Int,
    @SerializedName("orderTime") val orderDate: Date,
)