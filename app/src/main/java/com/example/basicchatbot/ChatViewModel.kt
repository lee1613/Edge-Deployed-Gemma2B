package com.example.basicchatbot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatbot = EdgeChatbot(application.applicationContext)

    // We store the full conversation transcript here
    private val transcript = StringBuilder("Bot: Bot is ready. Say hello!")
    
    private val _uiState = MutableStateFlow(transcript.toString())
    val uiState = _uiState.asStateFlow()

    fun sendMessage(userMessage: String) {
        // 1. Add User message to transcript
        transcript.append("\n\nYou: $userMessage")
        transcript.append("\n\nBot: Thinking...")
        _uiState.value = transcript.toString()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Measure the time and get response
                val (response, timeTaken) = chatbot.generateResponseWithTiming(userMessage)
                
                // Convert ms to seconds for better readability
                val seconds = timeTaken / 1000.0

                // 2. Replace "Thinking..." with actual response + timing info
                val startOfThinking = transcript.lastIndexOf("Bot: Thinking...")
                transcript.replace(startOfThinking, transcript.length, "Bot: $response\n(Generated in ${"%.2f".format(seconds)}s)")
                
                _uiState.value = transcript.toString()
            } catch (e: Exception) {
                val startOfThinking = transcript.lastIndexOf("Bot: Thinking...")
                transcript.replace(startOfThinking, transcript.length, "Bot: Error: ${e.message}")
                _uiState.value = transcript.toString()
            }
        }
    }
}