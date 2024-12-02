package com.ssafy.cafe_in_port_client.data.model.response

import com.google.gson.annotations.SerializedName
import com.ssafy.cafe_in_port_client.data.model.dto.Comment
import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.ProductType
import com.ssafy.cafe_in_port_client.common.enums.Temperature

data class ProductWithDetailResponse(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val productName: String = "",
    @SerializedName("type") val type: ProductType = ProductType.NO,
    @SerializedName("img") val productImg: String = "",
    @SerializedName("detailId") val detailId: Int,
    @SerializedName("size") val size: DrinkSize = DrinkSize.NO,
    @SerializedName("price") val price: Int,
    @SerializedName("temperature") val temperature: Temperature = Temperature.NO
)