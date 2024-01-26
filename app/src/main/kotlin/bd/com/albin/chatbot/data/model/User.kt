package bd.com.albin.chatbot.data.model

import android.net.Uri
import androidx.annotation.Keep

@Keep
data class User(
    val id: String = "",
    val isAnonymous: Boolean = true,
    val displayName: String? = null,
    val photoUrl: Uri? = null
)