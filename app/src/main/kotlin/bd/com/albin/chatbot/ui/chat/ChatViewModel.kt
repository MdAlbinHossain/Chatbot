package bd.com.albin.chatbot.ui.chat

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bd.com.albin.chatbot.data.repository.GenerativeContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: GenerativeContentRepository) :
    ViewModel() {

    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState.Initial)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun generateResponse(inputText: String, selectedImages: List<Bitmap>) {
        _uiState.value = ChatUiState.Loading

        viewModelScope.launch {
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
}