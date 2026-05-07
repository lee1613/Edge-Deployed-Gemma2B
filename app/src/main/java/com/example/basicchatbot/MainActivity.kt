package com.example.basicchatbot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputField = findViewById<EditText>(R.id.inputField)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val responseText = findViewById<TextView>(R.id.responseText)
        val scrollContainer = findViewById<ScrollView>(R.id.scrollContainer)

        // Observe the ViewModel state
        lifecycleScope.launch {
            viewModel.uiState.collect { text ->
                responseText.text = text
                // Auto-scroll to the bottom when new text arrives
                scrollContainer.post {
                    scrollContainer.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        }

        sendButton.setOnClickListener {
            val query = inputField.text.toString()
            if (query.isNotEmpty()) {
                viewModel.sendMessage(query)
                inputField.text.clear()
            }
        }
    }
}