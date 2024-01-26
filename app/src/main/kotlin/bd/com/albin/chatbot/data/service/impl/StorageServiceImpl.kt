package bd.com.albin.chatbot.data.service.impl

import bd.com.albin.chatbot.data.model.Message
import bd.com.albin.chatbot.data.service.AccountService
import bd.com.albin.chatbot.data.service.StorageService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService,
) : StorageService {
    override fun getMessagesStream(): Flow<List<Message>> =
        currentCollection().orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING).dataObjects()

    override suspend fun getMessage(messageId: String): Message? =
        currentCollection().document(messageId).get().await().toObject()

    @AddTrace(name = SAVE_MESSAGE_TRACE, enabled = true)
    override suspend fun save(message: Message): String {
        val updatedMessage = message.copy(userId = auth.currentUserId)
        return currentCollection().add(updatedMessage).await().id
    }

    @AddTrace(name = UPDATE_MESSAGE_TRACE, enabled = true)
    override suspend fun update(message: Message) {
        currentCollection().document(message.id).set(message).await()
    }

    override suspend fun delete(messageId: String) {
        currentCollection().document(messageId).delete().await()
    }

    private fun currentCollection(): CollectionReference = firestore.collection(
        USER_COLLECTION
    ).document(auth.currentUserId).collection(MESSAGE_COLLECTION)

    companion object {
        private const val CREATED_AT_FIELD = "createdAt"
        private const val MESSAGE_COLLECTION = "messages"
        private const val USER_COLLECTION = "users"
        private const val SAVE_MESSAGE_TRACE = "saveMessage"
        private const val UPDATE_MESSAGE_TRACE = "updateMessage"
    }
}