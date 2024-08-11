package bd.com.albin.chatbot.data.service.impl

import android.graphics.Bitmap
import bd.com.albin.chatbot.data.service.AiService
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.perf.metrics.AddTrace
import javax.inject.Inject

class AiServiceImpl @Inject constructor(
    private val geminiFlash: GenerativeModel) : AiService {
    @AddTrace(name = "generateContent", enabled = true)
    override suspend fun generateContent(prompt: String): String? {
        val response = geminiFlash.generateContent(prompt)
        return response.text
    }


    @AddTrace(name = "generateContentWithImage", enabled = true)
    override suspend fun generateContent(prompt: String, images: List<Bitmap>): String? {
        val inputContent = content {
            images.onEach {
                image(it)
            }
            text(prompt)
        }
        val response = geminiFlash.generateContent(inputContent)
        return response.text
    }
}