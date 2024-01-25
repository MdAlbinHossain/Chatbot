package bd.com.albin.chatbot.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    @DocumentId val id: String = "",
    @ServerTimestamp val createdAt: Date = Date(),
    val userId: String = "",
    val prompt: String = "",
    val images: List<String> = emptyList(),
    val response: String = ""
)