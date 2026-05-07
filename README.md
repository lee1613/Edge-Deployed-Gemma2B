# Basic Chatbot (On-Device AI)

A simple Android application demonstrating on-device Large Language Model (LLM) inference using Google's **Gemma 2B** model and **MediaPipe**.

## 🚀 Essence & Summary
This app is a privacy-first, offline-capable chatbot. Unlike typical AI apps that rely on cloud APIs (like ChatGPT or Gemini Pro), this application runs the entire AI model directly on your smartphone's hardware.

### How it's Implemented:
*   **Engine:** Uses the `MediaPipe LLM Inference API` for high-performance on-device execution.
*   **Model:** Utilizes the `Gemma 2B IT CPU Int4` model, which is optimized for mobile deployment.
*   **Architecture:** Follows the modern Android **MVVM** pattern using `ViewModel` and `StateFlow` for a reactive UI.
*   **Concurrency:** Heavy AI computations are offloaded to background threads using **Kotlin Coroutines** (`Dispatchers.IO`) to ensure the UI never freezes.
*   **Resource Management:** Implements a "Sliding Window" history to maintain conversational context without exceeding the model's memory (RAM) limits.

## ✨ Features
*   **100% Offline Inference:** No internet connection is required to chat with the AI.
*   **Conversational Memory:** The bot remembers previous parts of the conversation, allowing for natural, multi-turn threads.
*   **Performance Tracking:** Each response displays the exact time (in seconds) it took for the model to generate the text.
*   **Robust History Handling:** Automatically manages chat history to prevent app crashes while ensuring the most recent context is always available.
*   **Keyboard-Aware UI:** A clean interface that auto-scrolls to the latest message and resizes correctly when the keyboard appears.

## 🛠️ How to Run the App

### 1. Prerequisites
*   **Physical Android Device:** A real device is highly recommended. You need at least **4GB of RAM** (6GB+ preferred) as LLMs are memory-intensive.
*   **Android Studio:** Latest stable version (Hedgehog or newer).

### 2. Model Setup
Due to file size limits, the model file is not typically included in the repository source code.
*   Download the `gemma-2b-it-cpu-int4.bin` model (or similar compatible MediaPipe LLM).
*   Place the file in the following directory:
    `app/src/main/assets/`
*   Ensure the filename matches the one specified in `EdgeChatbot.kt`.

### 3. Build & Deploy
1.  Connect your Android device via USB.
2.  In Android Studio, click **Run 'app'**.
3.  **Note on First Run:** The very first time you launch the app, there will be a delay of several seconds. This is because the app is copying the ~1.3GB model from the assets folder to your internal storage to allow the AI engine to access it at maximum speed.

### 4. Configuration
You can adjust the AI's behavior in `EdgeChatbot.kt`:
*   `setMaxTokens`: Controls how long the response/memory can be.
*   `setTemperature`: Controls the "creativity" of the bot (higher is more random).
