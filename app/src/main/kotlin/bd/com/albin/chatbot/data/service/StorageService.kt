package bd.com.albin.chatbot.data.service

import bd.com.albin.chatbot.data.model.Message
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val messages: Flow<List<Message>>
    suspend fun getMessage(taskId: String): Message?
    suspend fun save(message: Message): String
    suspend fun update(message: Message)
    suspend fun delete(taskId: String)
}