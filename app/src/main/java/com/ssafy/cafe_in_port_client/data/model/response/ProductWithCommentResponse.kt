package com.ssafy.cafe_in_port_client.data.model.response

import com.google.gson.annotations.SerializedName
import com.ssafy.cafe_in_port_client.data.model.dto.Comment
import com.ssafy.cafe_in_port_client.common.enums.ProductType

data class ProductWithCommentResponse (
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val productName: String = "",
    @SerializedName("type") val type: ProductType = ProductType.NO,
    @SerializedName("img") val productImg: String = "",

    @SerializedName("commentCount") val productCommentTotalCnt: Int = 0,
    @SerializedName("totalSells") val productTotalSellCnt: Int = 0,
    @SerializedName("averageStars") val productRatingAvg: Double = 0.0,

    @SerializedName("comments") val comments : List<Comment> = emptyList()
)