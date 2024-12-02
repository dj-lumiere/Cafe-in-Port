package com.ssafy.cafe_in_port_client.data.model.dto

import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.Temperature
import com.ssafy.cafe_in_port_client.data.model.response.ProductWithCommentResponse
import com.ssafy.cafe_in_port_client.data.model.response.ProductWithDetailResponse

data class MenuDetailUiData(
    val productInfo: ProductWithCommentResponse? = null,
    val productDetails: List<ProductWithDetailResponse>? = null,
    val count: Int = 1,
    val drinkSize: DrinkSize? = null,
    val temperature: Temperature? = null,
    val productDetailId: Int = 0,
    val unitPrice: Int = 0,
)