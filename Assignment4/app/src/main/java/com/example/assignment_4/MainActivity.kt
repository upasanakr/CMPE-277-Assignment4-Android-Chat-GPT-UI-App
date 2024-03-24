package com.example.assignment_4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.example.assignment_4.ui.theme.Assignment4Theme
import kotlinx.coroutines.launch
import com.example.assignment_4.network.ChatGPTService
import com.example.assignment_4.network.ChatGPTRequest
import com.example.assignment_4.network.Message
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment4Theme {
                AppContent()
            }
        }
    }
}

fun initializeChatGPTApiService(): ChatGPTService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ChatGPTService::class.java)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    var prompt by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Enter your Prompt here") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(9.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        response = fetchGPTResponse(prompt)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Send")
            }
            Button(
                onClick = {
                    prompt = ""
                    response = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
        Spacer(modifier = Modifier.height(19.dp))
        Text(
            text = "Response From GPT: $response",
            modifier = Modifier.padding(9.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
        }
    }
}


suspend fun fetchGPTResponse(prompt: String): String {
    val chatGPTApiService = initializeChatGPTApiService()
    val requestBody = ChatGPTRequest(
        model = "gpt-3.5-turbo",
        messages = listOf(
            Message(role = "user", content = prompt)
        )
    )

    return try {
        val chatGPTApiResponse = chatGPTApiService.generateChatResponse(requestBody)
        if (chatGPTApiResponse.isSuccessful && chatGPTApiResponse.body() != null) {
            val chatGPTApiResponseBody = chatGPTApiResponse.body()
            if (chatGPTApiResponseBody != null && chatGPTApiResponseBody.choices.isNotEmpty()) {
                val message = chatGPTApiResponseBody.choices.first().message.content.trim()
                if (message.isNotEmpty()) {
                    message
                } else {
                    "Response was empty."
                }
            } else {
                "Received empty response. Response body: $chatGPTApiResponseBody"
            }
        } else {
            "Error: ${chatGPTApiResponse.errorBody()?.string()}"
        }
    } catch (e: Exception) {
        "Failed to connect API: ${e.localizedMessage}"
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    assignment_4Theme {
        AppContent()
    }
}
@Composable
fun assignment_4Theme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}