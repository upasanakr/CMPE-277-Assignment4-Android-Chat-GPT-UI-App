package com.example.assignment4

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Response

interface OpenAIService {
    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json", "Authorization: Bearer <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>")
    suspend fun createCompletion(@Body body: OpenAIRequest): Response<OpenAIResponse>
}

data class Message(
    val role: String,
    val content: String
)

data class OpenAIRequest(
    val model: String,
    val messages: List<Message>
)

data class OpenAIResponse(
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