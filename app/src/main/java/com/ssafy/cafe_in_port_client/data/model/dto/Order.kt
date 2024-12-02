package com.ssafy.cafe_in_port_client.data.model.dto

import com.ssafy.cafe_in_port_client.common.enums.OrderStatus
import java.util.Date

data class Order(
    var id: Int,
    var userNo: Int,
    var orderTable: String,
    var orderTime: Date?=null,
    var updateTime: Date?=null,
    var status: OrderStatus,
    val details: ArrayList<OrderDetail> = ArrayList()
) {

    constructor() : this(0, 0, "", null,null, OrderStatus.PENDING)
}
