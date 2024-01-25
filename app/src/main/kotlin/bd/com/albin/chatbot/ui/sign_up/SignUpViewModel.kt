package bd.com.albin.chatbot.ui.sign_up

import androidx.compose.runtime.mutableStateOf
import bd.com.albin.chatbot.LOGIN_SCREEN
import bd.com.albin.chatbot.R
import bd.com.albin.chatbot.SETTINGS_SCREEN
import bd.com.albin.chatbot.SIGN_UP_SCREEN
import bd.com.albin.chatbot.common.ext.isValidEmail
import bd.com.albin.chatbot.common.ext.isValidPassword
import bd.com.albin.chatbot.common.ext.passwordMatches
import bd.com.albin.chatbot.common.snackbar.SnackbarManager
import bd.com.albin.chatbot.data.service.AccountService
import bd.com.albin.chatbot.data.service.LogService
import bd.com.albin.chatbot.ui.ChatbotViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
  private val accountService: AccountService,
  logService: LogService
) : ChatbotViewModel(logService) {
  var uiState = mutableStateOf(SignUpUiState())
    private set

  private val email
    get() = uiState.value.email
  private val password
    get() = uiState.value.password


  fun onLoginClick(openScreen: (String) -> Unit) = openScreen(LOGIN_SCREEN)

  fun onEmailChange(newValue: String) {
    uiState.value = uiState.value.copy(email = newValue)
  }

  fun onPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(password = newValue)
  }

  fun onRepeatPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(repeatPassword = newValue)
  }

  fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(R.string.email_error)
      return
    }

    if (!password.isValidPassword()) {
      SnackbarManager.showMessage(R.string.password_error)
      return
    }

    if (!password.passwordMatches(uiState.value.repeatPassword)) {
      SnackbarManager.showMessage(R.string.password_match_error)
      return
    }

    launchCatching {
      accountService.linkAccount(email, password)
      openAndPopUp(SETTINGS_SCREEN, SIGN_UP_SCREEN)
    }
  }
}
