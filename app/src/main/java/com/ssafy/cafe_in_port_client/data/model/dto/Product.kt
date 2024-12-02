package com.ssafy.cafe_in_port_client.data.model.dto

import com.ssafy.cafe_in_port_client.common.enums.ProductType

data class Product(
    val id: Int,
    val name: String,
    val type: ProductType,
    val img: String,
    val comment: ArrayList<Comment> = ArrayList()
) {
    constructor() : this(0, "", ProductType.NO, "")
}
