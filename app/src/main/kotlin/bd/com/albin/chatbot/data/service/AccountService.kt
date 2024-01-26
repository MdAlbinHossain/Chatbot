package bd.com.albin.chatbot.data.service

import bd.com.albin.chatbot.data.Resource
import bd.com.albin.chatbot.data.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUserId: String
    val hasUser: Boolean

    val currentUser: Flow<User>

    fun signInWithGoogle(credential: AuthCredential): Flow<Resource<AuthResult>>
    suspend fun authenticate(email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun createAnonymousAccount()
    suspend fun linkAccount(email: String, password: String)
    suspend fun deleteAccount()
    suspend fun signOut()
}