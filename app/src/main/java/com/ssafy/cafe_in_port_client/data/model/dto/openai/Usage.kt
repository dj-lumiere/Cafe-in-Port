package com.ssafy.cafe_in_port_client.data.model.dto.openai

import com.google.gson.annotations.SerializedName

data class Usage(
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("completion_tokens_details") val completionTokensDetails: CompletionTokensDetails,
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)