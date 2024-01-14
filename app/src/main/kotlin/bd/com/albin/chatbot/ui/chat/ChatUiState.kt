package bd.com.albin.chatbot.ui.chat

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface ChatUiState {

    /**
     * Empty state when the screen is first shown
     */
    data object Initial : ChatUiState

    /**
     * Still loading
     */
    data object Loading : ChatUiState

    /**
     * Text has been generated
     */
    data class Success(
        val outputText: String
    ) : ChatUiState

    /**
     * There was an error generating text
     */
    data class Error(
        val errorMessage: String
    ) : ChatUiState
}