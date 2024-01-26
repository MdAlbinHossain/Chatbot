package bd.com.albin.chatbot.ui.chat

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import bd.com.albin.chatbot.SETTINGS_SCREEN
import bd.com.albin.chatbot.data.model.Message
import bd.com.albin.chatbot.data.service.AccountService
import bd.com.albin.chatbot.data.service.AiService
import bd.com.albin.chatbot.data.service.LogService
import bd.com.albin.chatbot.data.service.StorageService
import bd.com.albin.chatbot.ui.ChatbotViewModel
import bd.com.albin.chatbot.ui.settings.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AiService,
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService,
) : ChatbotViewModel(logService) {

    val userState:StateFlow<SettingsUiState> = accountService.currentUser.map {
        SettingsUiState(
            it.isAnonymous, it.displayName, it.photoUrl
        )
    }.stateIn(
        initialValue = SettingsUiState(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L)
    )

    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState.Initial)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    val messages: StateFlow<List<Message>> = storageService.getMessagesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

    fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

    fun generateResponse(
        inputText: String,
        selectedImages: List<Bitmap>,
        images: List<String>,
        resetScroll: suspend () -> Unit
    ) = launchCatching {
        _uiState.value = ChatUiState.Loading
        resetScroll()
        withContext(Dispatchers.IO) {
            try {
                val response = if (selectedImages.isNotEmpty()) {
                    repository.generateContent(inputText, selectedImages)
                } else repository.generateContent(inputText)

                response?.let { outputContent ->
                    storageService.save(
                        Message(
                            prompt = inputText,
//                            images = images,
                            response = outputContent
                        )
                    )
                    _uiState.value = ChatUiState.Initial
                    resetScroll()
                }
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error(e.localizedMessage ?: "")
                resetScroll()
            }
        }
    }
}