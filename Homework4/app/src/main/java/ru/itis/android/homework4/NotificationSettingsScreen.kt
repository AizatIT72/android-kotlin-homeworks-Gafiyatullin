package ru.itis.android.homework4

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNotificationCreated: (NotificationData) -> Unit,
    viewModel: MainViewModel
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var shouldExpand by remember { mutableStateOf(false) }
    var openOnClick by remember { mutableStateOf(false) }
    var hasReplyAction by remember { mutableStateOf(false) }
    var selectedPriority by remember { mutableStateOf(NotificationPriority.MEDIUM) }
    var isExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val priorityOptions = listOf(
        NotificationPriority.MIN to stringResource(R.string.min_priority),
        NotificationPriority.LOW to stringResource(R.string.low_priority),
        NotificationPriority.MEDIUM to stringResource(R.string.medium_priority),
        NotificationPriority.HIGH to stringResource(R.string.high_priority)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.notification_title)) },
            modifier = Modifier.fillMaxWidth(),
            isError = title.isEmpty()
        )
        if (title.isEmpty()) {
            Text(
                text = stringResource(R.string.title_required),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text(stringResource(R.string.notification_message)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 3
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.expand_notification))
            Switch(
                checked = shouldExpand,
                onCheckedChange = { shouldExpand = it },
                enabled = message.isNotEmpty()
            )
        }

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            OutlinedTextField(
                value = priorityOptions.find { it.first == selectedPriority }?.second ?: "",
                onValueChange = {},
                label = { Text(stringResource(R.string.priority)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                priorityOptions.forEach { (priority, displayName) ->
                    DropdownMenuItem(
                        text = { Text(displayName) },
                        onClick = {
                            selectedPriority = priority
                            isExpanded = false
                        }
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.open_on_click))
            Switch(
                checked = openOnClick,
                onCheckedChange = { openOnClick = it }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.add_reply_action))
            Switch(
                checked = hasReplyAction,
                onCheckedChange = { hasReplyAction = it }
            )
        }

        Button(
            onClick = {
                if (title.isNotEmpty()) {
                    val notificationData = NotificationData(
                        title = title,
                        message = message,
                        shouldExpand = shouldExpand,
                        priority = selectedPriority,
                        openOnClick = openOnClick,
                        hasReplyAction = hasReplyAction
                    )
                    onNotificationCreated(notificationData)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotEmpty()
        ) {
            Text(stringResource(R.string.create_notification))
        }
    }
}