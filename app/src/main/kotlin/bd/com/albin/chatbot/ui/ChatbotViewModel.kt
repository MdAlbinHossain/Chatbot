package bd.com.albin.chatbot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bd.com.albin.chatbot.common.snackbar.SnackbarManager
import bd.com.albin.chatbot.common.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import bd.com.albin.chatbot.data.service.LogService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class ChatbotViewModel(private val logService: LogService) : ViewModel() {
  fun launchCatching(snackbar: Boolean = true, block: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch(
      CoroutineExceptionHandler { _, throwable ->
        if (snackbar) {
          SnackbarManager.showMessage(throwable.toSnackbarMessage())
        }
        logService.logNonFatalCrash(throwable)
      },
      block = block
    )
}
