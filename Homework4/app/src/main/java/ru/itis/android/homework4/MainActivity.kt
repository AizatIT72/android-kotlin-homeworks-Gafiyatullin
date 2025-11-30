package ru.itis.android.homework4

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.RemoteInput
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var notificationHelper: NotificationHelper
    private var currentNotificationId = Constants.DEFAULT_NOTIFICATION_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationHelper = NotificationHelper(this)

        handleIntent(intent)

        setContent {
            NotificationsAppTheme {
                MainApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Constants.REPLY_ACTION) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            remoteInput?.getCharSequence(Constants.KEY_REPLY)?.let { reply ->
                val replyMessage = "${getString(R.string.reply_prefix)} $reply"
                viewModel.addMessage(replyMessage)
            }
        } else if (intent.hasExtra(getString(R.string.intent_extra_title)) &&
            intent.hasExtra(getString(R.string.intent_extra_message))) {
            val title = intent.getStringExtra(getString(R.string.intent_extra_title)) ?: ""
            val message = intent.getStringExtra(getString(R.string.intent_extra_message)) ?: ""
            val notificationMessage = "${getString(R.string.notification_prefix)} $title - $message"
            viewModel.addMessage(notificationMessage)
        }
    }

    @Composable
    private fun MainApp() {
        RequestNotificationPermission()
        val navController = rememberNavController()
        val context = LocalContext.current

        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination

        Scaffold(
            bottomBar = {
                NavigationBar {
                    listOf(
                        Screen.Settings,
                        Screen.Edit,
                        Screen.Messages
                    ).forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = stringResource(screen.title)
                                )
                            },
                            label = { Text(stringResource(screen.title)) },
                            selected = currentDestination?.route == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Settings.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Settings.route) {
                    NotificationSettingsScreen(
                        onNotificationCreated = { data ->
                            val id = currentNotificationId++
                            notificationHelper.createNotification(id, data) {
                            }
                            if (currentNotificationId > 1000) {
                                currentNotificationId = 1
                            }
                        },
                        viewModel = viewModel
                    )
                }
                composable(Screen.Edit.route) {
                    EditNotificationScreen(
                        notificationHelper = notificationHelper,
                        viewModel = viewModel
                    )
                }
                composable(Screen.Messages.route) {
                    UserMessagesScreen(viewModel = viewModel)
                }
            }
        }
    }
}

sealed class Screen(
    val route: String,
    val title: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Settings : Screen(
        route = "settings",
        title = R.string.settings_tab,
        icon = Icons.Default.Settings
    )

    object Edit : Screen(
        route = "edit",
        title = R.string.edit_tab,
        icon = Icons.Default.Edit
    )

    object Messages : Screen(
        route = "messages",
        title = R.string.messages_tab,
        icon = Icons.Default.Message
    )
}

@Composable
fun NotificationsAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}