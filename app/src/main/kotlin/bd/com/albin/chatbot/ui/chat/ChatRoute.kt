package bd.com.albin.chatbot.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import bd.com.albin.chatbot.data.model.Message
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
internal fun ChatRoute(
    viewModel: ChatViewModel = hiltViewModel(), openScreen: (String) -> Unit = {}
) {

    val chatUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ChatScreen(messages, chatUiState, scrollState, onSendClicked = { inputText, selectedImages ->
        keyboardController?.hide()
        coroutineScope.launch {
            val bitmaps = selectedImages.map { imageUriToBitmap(context, it) }
            viewModel.generateResponse(inputText, bitmaps, selectedImages.map { it.toString() }) {
                scrollState.scrollToItem(0)
            }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    messages: List<Message> = emptyList(),
    uiState: ChatUiState = ChatUiState.Initial,
    scrollState: LazyListState = rememberLazyListState(),
    onSendClicked: (String, List<Uri>) -> Unit = { _: String, _: List<Uri> -> },
    onSettingsClick: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
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

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.app_name))
            },
            scrollBehavior = scrollBehavior,
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            },
        )
    }, bottomBar = {
        Card(
            modifier = Modifier.padding(8.dp)
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
                        prompt = ""
                    }),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        onSendClicked(prompt, imageUris)
                        prompt = ""
                    },
                    enabled = (prompt.isNotBlank() || imageUris.isNotEmpty()) && (uiState != ChatUiState.Loading),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(imageVector = Icons.Outlined.Send, contentDescription = null)
                }
            }
        }
    }) { innerPadding ->
        SelectionContainer {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(end = 8.dp),
            ) {
                LazyColumn(
                    reverseLayout = true,
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .animateItemPlacement()
                                .fillMaxWidth()
                        ) {
                            if (uiState is ChatUiState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                            } else if (uiState is ChatUiState.Error) {
                                Text(
                                    text = uiState.errorMessage,
                                    color = Color.Red,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                    items(items = messages, key = { it.id }) {
                        Column(modifier = Modifier.animateItemPlacement()) {
                            Row {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 8.dp)
                                ) {
                                    Text(text = it.prompt, modifier = Modifier.padding(8.dp))
                                }
                            }
                            Row {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Text(text = it.response, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
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