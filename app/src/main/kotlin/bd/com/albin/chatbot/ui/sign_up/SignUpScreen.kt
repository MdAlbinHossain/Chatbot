package bd.com.albin.chatbot.ui.sign_up

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import bd.com.albin.chatbot.R
import bd.com.albin.chatbot.common.composable.BasicButton
import bd.com.albin.chatbot.common.composable.BasicTextButton
import bd.com.albin.chatbot.common.composable.EmailField
import bd.com.albin.chatbot.common.composable.PasswordField
import bd.com.albin.chatbot.common.composable.RepeatPasswordField
import bd.com.albin.chatbot.common.ext.basicButton
import bd.com.albin.chatbot.common.ext.fieldModifier
import bd.com.albin.chatbot.common.ext.textButton
import bd.com.albin.chatbot.ui.theme.ChatbotTheme

@Composable
fun SignUpScreen(
    openAndPopUp: (String, String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    SignUpScreenContent(uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRepeatPasswordChange = viewModel::onRepeatPasswordChange,
        onSignUpClick = { viewModel.onSignUpClick(openAndPopUp) },
        onGoToSignInClick = { viewModel.onLoginClick(openScreen) })
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onGoToSignInClick: () -> Unit
) {
    val fieldModifier = Modifier.fieldModifier()


    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(uiState.email, onEmailChange, fieldModifier)
        PasswordField(uiState.password, onPasswordChange, fieldModifier)
        RepeatPasswordField(uiState.repeatPassword, onRepeatPasswordChange, fieldModifier)

        BasicButton(R.string.create_account, Modifier.basicButton()) {
            onSignUpClick()
        }
        BasicTextButton(
            text = R.string.already_have_account, modifier = Modifier.textButton()
        ) {
            onGoToSignInClick()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val uiState = SignUpUiState(
        email = "email@test.com"
    )

    ChatbotTheme {
        SignUpScreenContent(uiState = uiState,
            onEmailChange = { },
            onPasswordChange = { },
            onRepeatPasswordChange = { },
            onSignUpClick = { },
            onGoToSignInClick = {})
    }
}
