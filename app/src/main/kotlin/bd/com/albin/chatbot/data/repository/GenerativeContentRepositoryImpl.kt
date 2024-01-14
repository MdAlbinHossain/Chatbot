package bd.com.albin.chatbot.data.repository

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject

class GenerativeContentRepositoryImpl @Inject constructor(
    private val geminiPro: GenerativeModel, private val geminiProVision: GenerativeModel
) : GenerativeContentRepository {
    override suspend fun generateContent(prompt: String): String? {
        val response = geminiPro.generateContent(prompt)
        return response.text
    }

    override suspend fun generateContent(prompt: String, images: List<Bitmap>): String? {
        val inputContent = content {
            images.onEach {
                image(it)
            }
            text(prompt)
        }
        val response = geminiProVision.generateContent(inputContent)
        return response.text
    }
}