package bd.com.albin.chatbot.data.service

import android.graphics.Bitmap

interface AiService {
    suspend fun generateContent(prompt: String): String?
    suspend fun generateContent(prompt: String, images: List<Bitmap>): String?
}