package bd.com.albin.chatbot.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bd.com.albin.chatbot.R
import bd.com.albin.chatbot.common.utils.imageUriToBitmap
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ChatRoute(
    viewModel: ChatViewModel = hiltViewModel(), openScreen: (String) -> Unit = {}
) {

    val chatUiState by viewModel.uiState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ChatScreen(chatUiState,   onSendClicked = { inputText, selectedImages ->
        keyboardController?.hide()
        coroutineScope.launch {
            val bitmaps = selectedImages.map { imageUriToBitmap(context, it) }
            viewModel.generateResponse(inputText, bitmaps, selectedImages.map { it.toString() })
        }
    }, onSettingsClick = { viewModel.onSettingsClick(openScreen) })
}

object CustomUriStateSaver : Saver<MutableList<Uri>, List<String>> {
    override fun restore(value: List<String>): MutableList<Uri> {
        return value.map { Uri.parse(it) }.toMutableList()
    }

    override fun SaverScope.save(value: MutableList<Uri>): List<String> {
        return value.map { it.toString() }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiState: ChatUiState = ChatUiState.Initial,
    onSendClicked: (String, List<Uri>) -> Unit = { _: String, _: List<Uri> -> },
    onSettingsClick: () -> Unit = {}
) {
    var prompt by rememberSaveable { mutableStateOf("") }
    val imageUris = rememberSaveable(saver = CustomUriStateSaver) {
        mutableStateListOf()
    }
    val pickImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
            it?.let {
                imageUris.add(it)
            }
        }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            })
    }, bottomBar = {
        Column(
            modifier = Modifier
                .padding()
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        ) {
            AnimatedVisibility(visible = imageUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    items(imageUris) {
                        Box {
                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(
                                        RoundedCornerShape(10)
                                    )
                                    .padding(end = 8.dp, top = 8.dp),
                                contentScale = ContentScale.FillBounds
                            )
                            IconButton(
                                onClick = { imageUris.remove(it) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(
                                        MaterialTheme.colorScheme.tertiaryContainer,
                                    )
                                    .size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = {
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }, modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = "Insert Photo Icon"
                    )
                }
                OutlinedTextField(value = prompt,
                    placeholder = { Text(stringResource(R.string.prompt_hint)) },
                    onValueChange = { prompt = it },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        onSendClicked(
                            prompt, imageUris
                        )
                        imageUris.clear()
                    }),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        onSendClicked(prompt, imageUris)
                        imageUris.clear()
                        prompt=""
                    },
                    enabled = prompt.isNotBlank() || imageUris.isNotEmpty(),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(imageVector = Icons.Outlined.Send, contentDescription = null)
                }
            }
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(all = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            when (uiState) {
                is ChatUiState.Initial -> {
                    Text(
                        text = stringResource(R.string.prompt_hints),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                    is ChatUiState.Loading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 8.dp)
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ChatUiState.Success -> {
                        Row {
                            Icon(
                                Icons.Default.SmartToy, contentDescription = "SmartToy Icon"
                            )
                            Text(
                                text = uiState.outputText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                is ChatUiState.Error -> {
                    Text(
                        text = uiState.errorMessage,
                        color = Color.Red
                    )
                }
            }
        }
    }

}


@Composable
@Preview(showSystemUi = true)
fun ChatScreenPreview() {
    ChatScreen()
}