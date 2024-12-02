package com.ssafy.cafe_in_port_client.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenAiApiClient {
    private const val BASE_URL = "https://api.openai.com/"

    private val client = OkHttpClient.Builder().build()

    val openAiService: OpenAiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiService::class.java)
    }
}
