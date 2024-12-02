package com.ssafy.cafe_in_port_client.data.model.response

import com.google.gson.annotations.SerializedName
import com.ssafy.cafe_in_port_client.data.model.dto.openai.Choice
import com.ssafy.cafe_in_port_client.data.model.dto.openai.Usage

data class ChatCompletionResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    @SerializedName("object") val aiObject: String,
    val usage: Usage
)