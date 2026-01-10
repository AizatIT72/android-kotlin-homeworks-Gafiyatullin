package ru.itis.android.homework5

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val scaffoldState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            scaffoldState.showSnackbar(message)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = scaffoldState) }
    ) { padding ->
        MainContent(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun MainContent(
    uiState: UiState,
    onEvent: (MainEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.coroutines_controller),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        CoroutineCountSlider(
            coroutineCount = uiState.coroutineCount,
            onCountChanged = { onEvent(MainEvent.CoroutineCountChanged(it)) }
        )

        DispatcherDropdown(
            selectedDispatcher = uiState.selectedDispatcher,
            onDispatcherChanged = { onEvent(MainEvent.DispatcherChanged(it)) }
        )

        LaunchSwitches(
            isSequential = uiState.isSequential,
            isParallel = uiState.isParallel,
            isLazy = uiState.isLazy,
            onSequentialChanged = { onEvent(MainEvent.SequentialChanged(it)) },
            onParallelChanged = { onEvent(MainEvent.ParallelChanged(it)) },
            onLazyChanged = { onEvent(MainEvent.LazyChanged(it)) }
        )

        if (uiState.isRunning) {
            ExecutionProgress(
                completed = uiState.completedCoroutines,
                total = uiState.totalCoroutines
            )
        }

        LaunchCancelButtons(
            isRunning = uiState.isRunning,
            showCancel = uiState.showCancel,
            onLaunchClicked = { onEvent(MainEvent.LaunchClicked) },
            onCancelClicked = { onEvent(MainEvent.CancelClicked) }
        )
    }
}

@Composable
private fun CoroutineCountSlider(
    coroutineCount: Float,
    onCountChanged: (Float) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.number_of_coroutines, coroutineCount.toInt()),
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = coroutineCount,
            onValueChange = onCountChanged,
            valueRange = Constants.MIN_COROUTINES.toFloat()..Constants.MAX_COROUTINES.toFloat(),
            steps = ((Constants.MAX_COROUTINES - Constants.MIN_COROUTINES) / Constants.STEP_COROUTINES - 1).toInt()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DispatcherDropdown(
    selectedDispatcher: DispatcherType,
    onDispatcherChanged: (DispatcherType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = stringResource(selectedDispatcher.displayNameRes),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.dispatcher)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DispatcherType.values().forEach { dispatcher ->
                DropdownMenuItem(
                    text = { Text(stringResource(dispatcher.displayNameRes)) },
                    onClick = {
                        onDispatcherChanged(dispatcher)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LaunchSwitches(
    isSequential: Boolean,
    isParallel: Boolean,
    isLazy: Boolean,
    onSequentialChanged: (Boolean) -> Unit,
    onParallelChanged: (Boolean) -> Unit,
    onLazyChanged: (Boolean) -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.sequential_launch))
                Switch(
                    checked = isSequential,
                    onCheckedChange = onSequentialChanged
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.parallel_launch))
                Switch(
                    checked = isParallel,
                    onCheckedChange = onParallelChanged
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.lazy_launch))
                Switch(
                    checked = isLazy,
                    onCheckedChange = onLazyChanged
                )
            }
        }
    }
}

@Composable
private fun ExecutionProgress(
    completed: Int,
    total: Int
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.progress, completed, total),
                style = MaterialTheme.typography.titleMedium
            )
            LinearProgressIndicator(
                progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LaunchCancelButtons(
    isRunning: Boolean,
    showCancel: Boolean,
    onLaunchClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!isRunning) {
            Button(
                onClick = onLaunchClicked,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isRunning
            ) {
                Text(stringResource(R.string.launch_coroutines))
            }
        }

        if (showCancel) {
            Button(
                onClick = onCancelClicked,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(stringResource(R.string.cancel_coroutines))
            }
        }
    }
}