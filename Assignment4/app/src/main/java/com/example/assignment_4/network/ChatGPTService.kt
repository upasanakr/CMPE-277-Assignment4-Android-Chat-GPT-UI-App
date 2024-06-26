package com.example.assignment_4.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Response

interface ChatGPTService {
    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json", "Authorization: Bearer <Enter your keys here>")//Enter your GPT  Keys here
    suspend fun generateChatResponse(@Body body: ChatGPTRequest): Response<ChatGPTResponse>
}

data class Message(
    val role: String,
    val content: String
)

data class ChatGPTRequest(
    val model: String,
    val messages: List<Message>
)

data class ChatGPTResponse(
    val id: String,
    val created: Int,
    val model: String,
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: MessageContent,
    val logprobs: Any?,
    val finish_reason: String
)

data class MessageContent(
    val role: String,
    val content: String
)