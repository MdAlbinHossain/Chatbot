package bd.com.albin.chatbot.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicToolbar(@StringRes title: Int) {
    TopAppBar(title = { Text(stringResource(title)) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionToolbar(
    modifier: Modifier,
    @StringRes title: Int,
    primaryActionIcon: ImageVector,
    primaryAction: () -> Unit,
    secondaryActionIcon: ImageVector? = null,
    secondaryAction: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(stringResource(title)) },
        actions = {
            Box(modifier) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                ) {
                    IconButton(onClick = primaryAction) {
                        Icon(
                            imageVector = primaryActionIcon,
                            contentDescription = "Primary Action"
                        )
                    }
                    if (secondaryAction != null && secondaryActionIcon != null) {
                        IconButton(onClick = secondaryAction) {
                            Icon(
                                imageVector = secondaryActionIcon,
                                contentDescription = "Secondary Action"
                            )
                        }
                    }
                }
            }
        }
    )
}