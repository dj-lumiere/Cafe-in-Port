package com.ssafy.cafe_in_port_client.data.remote

import android.media.VolumeShaper.Operation
import com.ssafy.cafe_in_port_client.data.model.dto.Order
import com.ssafy.cafe_in_port_client.data.model.dto.User
import com.ssafy.cafe_in_port_client.data.model.response.UserResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface UserService {
    // 사용자 정보를 추가한다.
    @POST("rest/user")
    suspend fun insert(@Body body: User): Boolean

    // 사용자의 정보와 함께 사용자의 주문 내역, 사용자 등급 정보를 반환한다.
    @GET("rest/user/info")
    suspend fun getUserInfo(@Query("id") id: String): UserResponse

    // request parameter로 전달된 id가 이미 사용중인지 반환한다.
    @GET("rest/user/isUsed")
    suspend fun isUsedId(@Query("id") id: String): Boolean

    // 로그인 처리 후 성공적으로 로그인 되었다면 loginId라는 쿠키를 내려준다.
    @POST("rest/user/login")
    suspend fun login(@Body body: User): Response<User>

    @GET("rest/user/isEmailDuplicate")
    suspend fun isEmailDuplicate(@Query("email") email: String): Boolean

    @POST("rest/user/findIdFromEmail")
    suspend fun findIdFromEmail(@Query("email") email: String): String

    @POST("rest/user/checkValidUser")
    suspend fun checkValidUser(@Body body: User): Int

    @POST("rest/user/changePassword")
    suspend fun changePassword(@Body body: User): Int
}