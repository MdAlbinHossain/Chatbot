package bd.com.albin.chatbot.di

import bd.com.albin.chatbot.BuildConfig
import bd.com.albin.chatbot.data.repository.GenerativeContentRepository
import bd.com.albin.chatbot.data.repository.GenerativeContentRepositoryImpl
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
    fun providesGenerativeContentRepository(): GenerativeContentRepository {
        val geminiPro = GenerativeModel(
            modelName = "gemini-pro", apiKey = BuildConfig.apiKey
        )
        val geminiProVision = GenerativeModel(
            modelName = "gemini-pro-vision", apiKey = BuildConfig.apiKey
        )
        return GenerativeContentRepositoryImpl(
            geminiPro = geminiPro, geminiProVision = geminiProVision
        )
    }
}