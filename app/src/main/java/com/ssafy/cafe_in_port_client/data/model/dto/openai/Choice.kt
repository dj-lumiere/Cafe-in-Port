package com.ssafy.cafe_in_port_client.data.model.dto.openai

import com.google.gson.annotations.SerializedName

data class Choice(
    @SerializedName("finish_reason") val finishReason: String,
    val index: Int,
    val logprobs: Any,
    val message: Message
)