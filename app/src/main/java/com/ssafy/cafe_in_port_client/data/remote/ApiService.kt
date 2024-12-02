package com.ssafy.cafe_in_port_client.data.remote

import com.ssafy.cafe_in_port_client.data.model.dto.User
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response

interface ApiService {
    @POST("rest/user/{userNo}/fcm-token")
    suspend fun updateFcmToken(
        @Path("userNo") userNo: Int,
        @Body tokenRequest: String
    ): Int

    @POST("/rest/user/login")
    suspend fun login(@Body user: User): Response<User>
}