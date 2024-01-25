package bd.com.albin.chatbot.ui.splash

import androidx.compose.runtime.mutableStateOf
import bd.com.albin.chatbot.CHAT_SCREEN
import bd.com.albin.chatbot.SPLASH_SCREEN
import bd.com.albin.chatbot.data.service.AccountService
import bd.com.albin.chatbot.data.service.LogService
import bd.com.albin.chatbot.ui.ChatbotViewModel
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
//  configurationService: ConfigurationService,
  private val accountService: AccountService,
  logService: LogService
) : ChatbotViewModel(logService) {
  val showError = mutableStateOf(false)

//  init {
//    launchCatching { configurationService.fetchConfiguration() }
//  }

  fun onAppStart(openAndPopUp: (String, String) -> Unit) {

    showError.value = false
    if (accountService.hasUser) openAndPopUp(CHAT_SCREEN, SPLASH_SCREEN)
    else createAnonymousAccount(openAndPopUp)
  }


  private fun createAnonymousAccount(openAndPopUp: (String, String) -> Unit) {
    launchCatching {
      try {
        accountService.createAnonymousAccount()
      } catch (ex: FirebaseAuthException) {
        showError.value = true
        throw ex
      }
      openAndPopUp(CHAT_SCREEN, SPLASH_SCREEN)
    }
  }
}
