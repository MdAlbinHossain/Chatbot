package bd.com.albin.chatbot.ui.settings

import bd.com.albin.chatbot.LOGIN_SCREEN
import bd.com.albin.chatbot.SIGN_UP_SCREEN
import bd.com.albin.chatbot.SPLASH_SCREEN
import bd.com.albin.chatbot.data.service.AccountService
import bd.com.albin.chatbot.data.service.LogService
import bd.com.albin.chatbot.data.service.StorageService
import bd.com.albin.chatbot.ui.ChatbotViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  logService: LogService,
  private val accountService: AccountService,
  private val storageService: StorageService
) : ChatbotViewModel(logService) {
  val uiState = accountService.currentUser.map { SettingsUiState(it.isAnonymous, it.displayName, it.photoUrl) }

  fun onLoginClick(openScreen: (String) -> Unit) = openScreen(LOGIN_SCREEN)

  fun onSignUpClick(openScreen: (String) -> Unit) = openScreen(SIGN_UP_SCREEN)

  fun onSignOutClick(restartApp: (String) -> Unit) {
    launchCatching {
      accountService.signOut()
      restartApp(SPLASH_SCREEN)
    }
  }

  fun onDeleteMyAccountClick(restartApp: (String) -> Unit) {
    launchCatching {
//      storageService.deleteAllMessages()
      accountService.deleteAccount()
      restartApp(SPLASH_SCREEN)
    }
  }
}
