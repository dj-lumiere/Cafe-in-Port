package com.ssafy.cafe_in_port_client.data.model.dto

data class Comment(
    val commentId: Int = -1,
    val userNo: Int,
    val productId: Int,
    val rating: Float,
    var comment: String,
    val userName: String = ""
)