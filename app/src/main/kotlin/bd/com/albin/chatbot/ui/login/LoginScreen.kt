package bd.com.albin.chatbot.ui.login

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import bd.com.albin.chatbot.common.ext.basicButton
import bd.com.albin.chatbot.common.ext.fieldModifier
import bd.com.albin.chatbot.common.ext.spacer
import bd.com.albin.chatbot.common.ext.textButton
import bd.com.albin.chatbot.ui.theme.ChatbotTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun LoginScreen(
    openAndPopUp: (String, String) -> Unit,
    openScreen: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    val googleSignInState = viewModel.googleState.value

    val context = LocalContext.current
    val state = viewModel.signInState.collectAsState(initial = null)

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
        .requestProfile().requestIdToken(BuildConfig.webClientId).build()
    val googleSingInClient = GoogleSignIn.getClient(context, gso)
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            viewModel.onGoogleSignIn(it, openAndPopUp)
        }

    LaunchedEffect(key1 = state.value?.isSuccess) {
        if (state.value?.isSuccess?.isNotEmpty() == true) {
            val success = state.value?.isSuccess
            Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(key1 = state.value?.isError) {
        if (state.value?.isError?.isNotEmpty() == true) {
            val error = state.value?.isError
            Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(key1 = googleSignInState.success) {
        if (googleSignInState.success != null) {
            Toast.makeText(context, "Sign In Success", Toast.LENGTH_LONG).show()
        }

    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        if (googleSignInState.loading) {
            LinearProgressIndicator()
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignInClick = { viewModel.onSignInClick(openAndPopUp) },
        onGoogleSignInClick = { launcher.launch(googleSingInClient.signInIntent) },
        onGoToSignUPClick = { viewModel.onSignUpClick(openScreen) },
        onForgotPasswordClick = viewModel::onForgotPasswordClick
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onGoToSignUPClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    BasicToolbar(R.string.login_details)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(uiState.email, onEmailChange, Modifier.fieldModifier())
        PasswordField(uiState.password, onPasswordChange, Modifier.fieldModifier())

        BasicButton(R.string.sign_in, Modifier.basicButton()) { onSignInClick() }

        Spacer(modifier = Modifier.spacer())

        Text(text = "Or")

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

        BasicTextButton(R.string.forgot_password, Modifier.textButton()) {
            onForgotPasswordClick()
        }

        BasicTextButton(
            text = R.string.don_t_have_account, modifier = Modifier.textButton()
        ) {
            onGoToSignUPClick()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val uiState = LoginUiState(
        email = "email@test.com"
    )

    ChatbotTheme {
        LoginScreenContent(uiState = uiState,
            onEmailChange = { },
            onPasswordChange = { },
            onSignInClick = { },
            onGoToSignUPClick = {},
            onForgotPasswordClick = { },
            onGoogleSignInClick = {})
    }
}
