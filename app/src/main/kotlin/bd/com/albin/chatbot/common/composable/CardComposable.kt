package bd.com.albin.chatbot.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bd.com.albin.chatbot.common.ext.dropdownSelector

@Composable
fun DangerousCardEditor(
  @StringRes title: Int,
  icon: ImageVector,
  content: String,
  modifier: Modifier,
  onEditClick: () -> Unit
) {
  CardEditor(title, icon, content, onEditClick, MaterialTheme.colorScheme.primary, modifier)
}

@Composable
fun RegularCardEditor(
    @StringRes title: Int,
    icon: ImageVector,
    content: String,
    modifier: Modifier,
    onEditClick: () -> Unit
) {
  CardEditor(title, icon, content, onEditClick, MaterialTheme.colorScheme.onSurface, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardEditor(
  @StringRes title: Int,
  icon: ImageVector,
  content: String,
  onEditClick: () -> Unit,
  highlightColor: Color,
  modifier: Modifier
) {
  Card(

    modifier = modifier,
    onClick = onEditClick
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) { Text(stringResource(title), color = highlightColor) }

      if (content.isNotBlank()) {
        Text(text = content, modifier = Modifier.padding(16.dp, 0.dp))
      }

      Icon(imageVector = icon, contentDescription = "Icon", tint = highlightColor)
    }
  }
}

@Composable
fun CardSelector(
  @StringRes label: Int,
  options: List<String>,
  selection: String,
  modifier: Modifier,
  onNewValue: (String) -> Unit
) {
  Card( modifier = modifier) {
    DropdownSelector(label, options, selection, Modifier.dropdownSelector(), onNewValue)
  }
}
