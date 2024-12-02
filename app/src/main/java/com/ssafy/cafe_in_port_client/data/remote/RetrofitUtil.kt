package com.ssafy.cafe_in_port_client.data.remote

import com.ssafy.cafe_in_port_client.base.ApplicationClass

object RetrofitUtil {
    val commentService = ApplicationClass.retrofit.create(CommentService::class.java)
    val orderService = ApplicationClass.retrofit.create(OrderService::class.java)
    val productService = ApplicationClass.retrofit.create(ProductService::class.java)
    val userService = ApplicationClass.retrofit.create(UserService::class.java)
    val apiService = ApplicationClass.retrofit.create(ApiService::class.java)
    val openAiService = ApplicationClass.retrofitForAi.create(OpenAiService::class.java)
}