package com.ssafy.cafe_in_port_client.data.model.dto

data class Stamp (
    val id: Int,
    val userNo: Int,
    val orderId: Int,
    val quantity: Int,
){
    constructor():this(0,0,0,0)
}

