package com.example.assignment4

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.example.assignment4.ui.theme.Assignment4Theme
import kotlinx.coroutines.launch
import com.example.assignment4.OpenAIService
import com.example.assignment4.OpenAIRequest
import com.example.assignment4.Message
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.ui.unit.dp



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

fun createRetrofitService(): OpenAIService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(OpenAIService::class.java)
}

@Composable
fun AppContent() {
    var prompt by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Prompt") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        response = callOpenAI(prompt)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Send")
            }
            Button(
                onClick = {
                    prompt = ""
                    response = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Text(response, modifier = Modifier.padding(8.dp).fillMaxWidth())
        }
    }
}


suspend fun callOpenAI(prompt: String): String {
    val service = createRetrofitService()
    val requestBody = OpenAIRequest(
        model = "gpt-3.5-turbo",
        messages = listOf(
            Message(role = "user", content = prompt)
        )
    )

    return try {
        val response = service.createCompletion(requestBody)
        if (response.isSuccessful && response.body() != null) {
            val responseBody = response.body()
            if (responseBody != null && responseBody.choices.isNotEmpty()) {
                val assistantMessage = responseBody.choices.first().message.content.trim()
                if (assistantMessage.isNotEmpty()) {
                    assistantMessage
                } else {
                    "The assistant's response was empty."
                }
            } else {
                "Received an empty response. Response body: $responseBody"
            }
        } else {
            "Error: ${response.errorBody()?.string()}"
        }
    } catch (e: Exception) {
        "Failed to connect to the API: ${e.localizedMessage}"
    }
}
@Preview(showBackground = true)
@Composable
fun AppContentPreview() {
    Assignment4Theme {
        AppContent()
    }
}