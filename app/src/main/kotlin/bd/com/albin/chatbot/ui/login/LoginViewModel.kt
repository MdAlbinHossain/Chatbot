package bd.com.albin.chatbot.ui.login

import androidx.activity.result.ActivityResult
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import bd.com.albin.chatbot.LOGIN_SCREEN
import bd.com.albin.chatbot.R
import bd.com.albin.chatbot.SETTINGS_SCREEN
import bd.com.albin.chatbot.SIGN_UP_SCREEN
import bd.com.albin.chatbot.common.ext.isValidEmail
import bd.com.albin.chatbot.common.snackbar.SnackbarManager
import bd.com.albin.chatbot.data.Resource
import bd.com.albin.chatbot.data.service.AccountService
import bd.com.albin.chatbot.data.service.LogService
import bd.com.albin.chatbot.ui.ChatbotViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService, logService: LogService
) : ChatbotViewModel(logService) {
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    private val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    private val _googleState = mutableStateOf(GoogleSignInState())
    val googleState: State<GoogleSignInState> = _googleState


    fun onSignUpClick(openScreen: (String) -> Unit) = openScreen(SIGN_UP_SCREEN)

    fun onGoogleSignIn(activityResult: ActivityResult, openAndPopUp: (String, String) -> Unit) =
        launchCatching {
            val account = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            val googleResult = account.getResult(ApiException::class.java)
            val credentials = GoogleAuthProvider.getCredential(googleResult.idToken, null)
            accountService.googleSignIn(credentials).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _googleState.value = GoogleSignInState(success = result.data)
                        openAndPopUp(SETTINGS_SCREEN, LOGIN_SCREEN)
                    }

                    is Resource.Loading -> {
                        _googleState.value = GoogleSignInState(loading = true)
                    }

                    is Resource.Error -> {
                        _googleState.value = GoogleSignInState(error = result.message!!)
                    }
                }
            }
        }

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }

        if (password.isBlank()) {
            SnackbarManager.showMessage(R.string.empty_password_error)
            return
        }

        launchCatching {
            accountService.authenticate(email, password)
            openAndPopUp(SETTINGS_SCREEN, LOGIN_SCREEN)
        }
    }

    fun onForgotPasswordClick() {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }

        launchCatching {
            accountService.sendRecoveryEmail(email)
            SnackbarManager.showMessage(R.string.recovery_email_sent)
        }
    }
}
