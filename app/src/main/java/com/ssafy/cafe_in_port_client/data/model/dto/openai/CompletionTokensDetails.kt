package com.ssafy.cafe_in_port_client.data.model.dto.openai

import com.google.gson.annotations.SerializedName

data class CompletionTokensDetails(
    @SerializedName("accepted_prediction_tokens") val acceptedPredictionTokens: Int,
    @SerializedName("reasoning_tokens") val reasoningTokens: Int,
    @SerializedName("rejected_prediction_tokens") val rejectedPredictionTokens: Int
)