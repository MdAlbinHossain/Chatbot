package bd.com.albin.chatbot.data.service.impl

import bd.com.albin.chatbot.data.model.Message
import bd.com.albin.chatbot.data.service.AccountService
import bd.com.albin.chatbot.data.service.StorageService
import bd.com.albin.chatbot.data.service.trace
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService,
) : StorageService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val messages: Flow<List<Message>> = auth.currentUser.flatMapLatest { user ->
        firestore.collection(MESSAGE_COLLECTION).whereEqualTo(USER_ID_FIELD, user.id)
            .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING).dataObjects()
    }

    override suspend fun getMessage(taskId: String): Message? =
        firestore.collection(MESSAGE_COLLECTION).document(taskId).get().await().toObject()

    override suspend fun save(message: Message): String = trace(SAVE_MESSAGE_TRACE) {
        val updatedMessage = message.copy(userId = auth.currentUserId)
        firestore.collection(MESSAGE_COLLECTION).add(updatedMessage).await().id
    }

    override suspend fun update(message: Message): Unit = trace(UPDATE_MESSAGE_TRACE) {
        firestore.collection(MESSAGE_COLLECTION).document(message.id).set(message).await()
    }

    override suspend fun delete(taskId: String) {
        firestore.collection(MESSAGE_COLLECTION).document(taskId).delete().await()
    }

    companion object {
        private const val USER_ID_FIELD = "userId"
        private const val CREATED_AT_FIELD = "createdAt"
        private const val MESSAGE_COLLECTION = "messages"
        private const val SAVE_MESSAGE_TRACE = "saveMessage"
        private const val UPDATE_MESSAGE_TRACE = "updateMessage"
    }
}