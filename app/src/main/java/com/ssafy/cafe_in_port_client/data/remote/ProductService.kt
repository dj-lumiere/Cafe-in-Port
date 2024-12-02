package com.ssafy.cafe_in_port_client.data.remote

import com.ssafy.cafe_in_port_client.data.model.dto.Product
import com.ssafy.cafe_in_port_client.common.enums.DrinkSize
import com.ssafy.cafe_in_port_client.common.enums.Temperature
import com.ssafy.cafe_in_port_client.data.model.response.ProductWithCommentResponse
import com.ssafy.cafe_in_port_client.data.model.response.ProductWithDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductService {
    // 전체 상품의 목록을 반환한다
    @GET("rest/product")
    suspend fun getProductList(): List<Product>

    // 전체 상품의 목록을 반환한다
    @GET("rest/product/{productId}")
    suspend fun selectProductById(@Path("productId") productId: Int): Product

    // {productId}에 해당하는 상품의 정보를 comment와 함께 반환한다.
    // comment 조회시 사용
    @GET("rest/product/{productId}/comments")
    suspend fun getProductWithComments(@Path("productId") productId: Int): ProductWithCommentResponse

    @GET("rest/product/{productId}/details")
    suspend fun getProductWithDetail(@Path("productId") productId: Int): List<ProductWithDetailResponse>

    @GET("rest/product/{productId}/{size}/{temperature}")
    suspend fun getProductDetailId(
        @Path("productId") productId: Int,
        @Path("size") size: DrinkSize,
        @Path("temperature") temperature: Temperature
    ): Int


}