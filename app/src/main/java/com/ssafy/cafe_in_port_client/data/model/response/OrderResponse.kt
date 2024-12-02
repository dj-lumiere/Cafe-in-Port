package com.ssafy.cafe_in_port_client.data.model.response

import com.google.gson.annotations.SerializedName
import com.ssafy.cafe_in_port_client.common.enums.OrderStatus
import java.util.Date

data class OrderResponse(
    @SerializedName("id") val orderId: Int = 0,
    @SerializedName("userNo") val userNo: Int = 0,
    @SerializedName("orderTable") val orderTable: String = "",
    @SerializedName("orderTime") val orderTime: Date = Date(),
    @SerializedName("status") val orderCompleted: OrderStatus = OrderStatus.PENDING,
    @SerializedName("details") var details: List<OrderDetailResponse> = emptyList(),

    var totalPrice: Int = 0,
    var orderCount: Int = 0
)