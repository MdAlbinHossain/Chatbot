package bd.com.albin.chatbot.ui.sign_up

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bd.com.albin.chatbot.BuildConfig
import bd.com.albin.chatbot.R
import bd.com.albin.chatbot.common.composable.BasicButton
import bd.com.albin.chatbot.common.composable.BasicTextButton
import bd.com.albin.chatbot.common.composable.BasicToolbar
import bd.com.albin.chatbot.common.composable.EmailField
import bd.com.albin.chatbot.common.composable.PasswordField
import bd.com.albin.chatbot.common.composable.RepeatPasswordField
import bd.com.albin.chatbot.common.ext.basicButton
import bd.com.albin.chatbot.common.ext.fieldModifier
import bd.com.albin.chatbot.common.ext.spacer
import bd.com.albin.chatbot.common.ext.textButton
import bd.com.albin.chatbot.ui.theme.ChatbotTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun SignUpScreen(
    openAndPopUp: (String, String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    val googleSignInState = viewModel.googleState.value

    val context = LocalContext.current

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
        .requestProfile().requestIdToken(BuildConfig.webClientId).build()
    val googleSingInClient = GoogleSignIn.getClient(context, gso)
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            viewModel.onGoogleSignIn(it, openAndPopUp)
        }

    if (googleSignInState.loading) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            LinearProgressIndicator()
        }
    }

    SignUpScreenContent(uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRepeatPasswordChange = viewModel::onRepeatPasswordChange,
        onSignUpClick = { viewModel.onSignUpClick(openAndPopUp) },
        onGoogleSignInClick = { launcher.launch(googleSingInClient.signInIntent) },
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
    onGoogleSignInClick: () -> Unit,
    onGoToSignInClick: () -> Unit
) {
    val fieldModifier = Modifier.fieldModifier()
    BasicToolbar(title = R.string.create_account)

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

        Spacer(modifier = Modifier.spacer())

        Text(text = stringResource(R.string.or))

        Spacer(modifier = Modifier.spacer())

        Button(
            onClick = onGoogleSignInClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.android_neutral_rd_ctn),
                contentDescription = null,
            )
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
            onGoToSignInClick = {},
            onGoogleSignInClick = {})
    }
}
