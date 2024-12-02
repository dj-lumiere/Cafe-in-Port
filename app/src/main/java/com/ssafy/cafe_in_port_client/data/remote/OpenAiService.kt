package com.ssafy.cafe_in_port_client.data.remote

import com.ssafy.cafe_in_port_client.data.model.dto.openai.ChatCompletionRequest
import com.ssafy.cafe_in_port_client.data.model.response.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}