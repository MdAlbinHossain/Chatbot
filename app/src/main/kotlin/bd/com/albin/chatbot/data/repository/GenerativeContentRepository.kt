package bd.com.albin.chatbot.data.repository

import android.graphics.Bitmap

interface GenerativeContentRepository {
    suspend fun generateContent(prompt: String): String?
    suspend fun generateContent(prompt: String, images: List<Bitmap>): String?
}