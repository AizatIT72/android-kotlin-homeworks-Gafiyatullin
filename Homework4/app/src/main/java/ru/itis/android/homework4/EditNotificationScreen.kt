package ru.itis.android.homework4

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNotificationScreen(
    notificationHelper: NotificationHelper,
    viewModel: MainViewModel
) {
    var notificationId by remember { mutableStateOf("") }
    var newMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = notificationId,
            onValueChange = { notificationId = it },
            label = { Text(stringResource(R.string.notification_id)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.enter_id_placeholder)) }
        )

        OutlinedTextField(
            value = newMessage,
            onValueChange = { newMessage = it },
            label = { Text(stringResource(R.string.new_message)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 3
        )

        Button(
            onClick = {
                if (notificationId.isNotEmpty() && newMessage.isNotEmpty()) {
                    val id = notificationId.toIntOrNull()
                    if (id != null) {
                        val success = notificationHelper.updateNotification(id, newMessage)
                        if (success) {
                            android.widget.Toast.makeText(
                                context,
                                context.getString(R.string.notification_updated),
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            newMessage = ""
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                context.getString(R.string.notification_not_found),
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = notificationId.isNotEmpty() && newMessage.isNotEmpty()
        ) {
            Text(stringResource(R.string.update_notification))
        }

        Button(
            onClick = {
                notificationHelper.dismissAllNotifications()
                val message = if (notificationHelper.hasActiveNotifications()) {
                    context.getString(R.string.no_notifications)
                } else {
                    context.getString(R.string.no_notifications)
                }
                android.widget.Toast.makeText(
                    context,
                    message,
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(stringResource(R.string.dismiss_all))
        }
    }
}