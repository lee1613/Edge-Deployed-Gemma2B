package com.example.basicchatbot

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File
import java.io.FileOutputStream
import kotlin.system.measureTimeMillis

// This class handles the actual AI work
class EdgeChatbot(context: Context) {
    private var llmInference: LlmInference

    // We use a List to manage history so we can easily "forget" old messages
    // to stay within the AI's token limits.
    private val historyEntries = mutableListOf<String>()
    
    // We keep the last 4-5 exchanges (user + bot) to maintain context
    private val MAX_HISTORY_MESSAGES = 10 

    init {
        val modelPath = getModelPath(context, "gemma-2b-it-cpu-int4.bin")

        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelPath)
            .setMaxTokens(1024) // Increased to 1024 for a larger "active" memory
            .setTemperature(0.7f)
            .setRandomSeed(1)
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    private fun getModelPath(context: Context, modelName: String): String {
        val modelFile = File(context.filesDir, modelName)
        if (!modelFile.exists()) {
            context.assets.open(modelName).use { inputStream ->
                FileOutputStream(modelFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return modelFile.absolutePath
    }

    /**
     * Generates a response and returns it along with the time taken in milliseconds.
     */
    fun generateResponseWithTiming(userMessage: String): Pair<String, Long> {
        // 1. Build prompt using only the most recent context
        val recentHistory = historyEntries.takeLast(MAX_HISTORY_MESSAGES).joinToString("")
        val prompt = "$recentHistory<start_of_turn>user\n$userMessage<end_of_turn>\n<start_of_turn>model\n"

        // 2. Run inference and measure time
        var response: String
        val timeTaken = measureTimeMillis {
            response = llmInference.generateResponse(prompt)
        }

        // 3. Update history for next time
        historyEntries.add("<start_of_turn>user\n$userMessage<end_of_turn>\n")
        historyEntries.add("<start_of_turn>model\n$response<end_of_turn>\n")
        
        // Trim the list so it doesn't grow indefinitely in memory
        if (historyEntries.size > 20) {
            historyEntries.removeAt(0)
            historyEntries.removeAt(0)
        }

        return Pair(response, timeTaken)
    }

    // Keeping the original for backward compatibility if needed
    fun generateResponse(userMessage: String): String {
        return generateResponseWithTiming(userMessage).first
    }
}