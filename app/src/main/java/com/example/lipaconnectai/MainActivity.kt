package com.example.lipaconnectai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var userPrompt by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Initialize Gemini 1.5 Flash Model
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-3.5-flash-lite",
            apiKey = "AQ.Ab8RN6ITXRwuzHcZK1yx3zUcpE_lhxXP7IXqrAFYJYBTUoxUIA"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LipaConnect AI",
                fontSize = 22.sp,
                color = Color.Black
            )
            TextButton(onClick = { /* Handle logout */ }) {
                Text(text = "Logout", color = Color.Gray)
            }
        }

        // Response / Chat Area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            if (aiResponse.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0E6FF), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = aiResponse,
                        color = Color(0xFF4A148C),
                        fontSize = 14.sp
                    )
                }
            } else if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0E6FF), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🤖 AI Assistant Ready\nHow can LipaConnect help you today?",
                        color = Color(0xFF4A148C),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Input Field and Send Button
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = userPrompt,
                onValueChange = { userPrompt = it },
                placeholder = { Text("Ask LipaConnect anything...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (userPrompt.isNotBlank() && !isLoading) {
                        val promptToSend = userPrompt
                        userPrompt = ""
                        isLoading = true

                        coroutineScope.launch {
                            try {
                                val response = generativeModel.generateContent(promptToSend)
                                aiResponse = response.text ?: "No response received from Gemini."
                            } catch (e: Exception) {
                                aiResponse = "Error: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = userPrompt.isNotBlank() && !isLoading
            ) {
                Text(if (isLoading) "Thinking..." else "Send Prompt")
            }
        }
    }
}