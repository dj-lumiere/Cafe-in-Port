package com.ssafy.cafe_in_port_client.data.model.dto.openai

data class ChatCompletionRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<Message>,
    val temperature: Double = 0.7
)