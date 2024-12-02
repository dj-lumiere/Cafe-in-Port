package com.ssafy.cafe_in_port_client.data.model.dto

import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.ProductType
import com.ssafy.cafe_in_port_client.common.enums.Temperature

data class ProductDetail(
    var detailId: Int,
    var name: String,
    var type: ProductType,
    var size: DrinkSize,
    var temperature: Temperature,
    var price: Int,
)