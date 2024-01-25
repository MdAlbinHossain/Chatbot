package bd.com.albin.chatbot.ui.settings

import android.net.Uri

data class SettingsUiState(
    val isAnonymousAccount: Boolean = true,
    val displayName: String? = null,
    val photoUrl: Uri? = null
)
