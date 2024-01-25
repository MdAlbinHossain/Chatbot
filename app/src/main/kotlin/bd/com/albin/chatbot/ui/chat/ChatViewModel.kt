package bd.com.albin.chatbot.ui.chat

import android.graphics.Bitmap
import bd.com.albin.chatbot.SETTINGS_SCREEN
import bd.com.albin.chatbot.data.service.AiService
import bd.com.albin.chatbot.data.service.LogService
import bd.com.albin.chatbot.ui.ChatbotViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AiService,
    logService: LogService
) : ChatbotViewModel(logService) {

    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState.Initial)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

    fun generateResponse(inputText: String, selectedImages: List<Bitmap>, images: List<String>) =
        launchCatching {
            _uiState.value = ChatUiState.Loading
            withContext(Dispatchers.IO) {
                try {
                    val response = if (selectedImages.isNotEmpty()) {
                        repository.generateContent(inputText, selectedImages)
                    } else repository.generateContent(inputText)

                    response?.let { outputContent ->
                        _uiState.value = ChatUiState.Success(outputContent)
                    }
                } catch (e: Exception) {
                    _uiState.value = ChatUiState.Error(e.localizedMessage ?: "")
                }
            }
        }
}