package bd.com.albin.chatbot.di

import bd.com.albin.chatbot.BuildConfig
import bd.com.albin.chatbot.data.service.AiService
import bd.com.albin.chatbot.data.service.impl.AiServiceImpl
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesGenerativeContentRepository(): AiService {
        val geminiPro = GenerativeModel(
            modelName = "gemini-pro", apiKey = BuildConfig.apiKey
        )
        val geminiProVision = GenerativeModel(
            modelName = "gemini-pro-vision", apiKey = BuildConfig.apiKey
        )
        return AiServiceImpl(
            geminiPro = geminiPro, geminiProVision = geminiProVision
        )
    }
}