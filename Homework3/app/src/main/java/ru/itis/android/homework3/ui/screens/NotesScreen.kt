package ru.itis.android.homework3.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.itis.android.homework3.R
import ru.itis.android.homework3.constants.AppConstants
import ru.itis.android.homework3.model.Note
import ru.itis.android.homework3.ui.theme.ThemeManager
import ru.itis.android.homework3.util.dimensionResource
import ru.itis.android.homework3.util.stringResource
import ru.itis.android.homework3.viewmodel.NotesViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun NotesScreen(
    navController: NavController,
    email: String,
    currentColorScheme: ColorScheme,
    onThemeChange: (ColorScheme) -> Unit = {}
) {
    val notesViewModel: NotesViewModel = viewModel(
        viewModelStoreOwner = navController.getBackStackEntry(AppConstants.Navigation.NOTES_SCREEN)
    )
    val notes = notesViewModel.notes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_large))
    ) {
        ThemeSelector(
            currentColorScheme = currentColorScheme,
            onThemeChange = onThemeChange
        )

        Text(
            text = stringResource(R.string.notes_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_large))
        )

        if (notes.isEmpty()) {
            Text(
                text = stringResource(R.string.no_notes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.notes_empty_padding)),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                items(notes) { note ->
                    NoteItem(note = note)
                }
            }
        }

        Button(
            onClick = {
                navController.navigate(AppConstants.Navigation.ADD_NOTE_SCREEN)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.add_note_button))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(note: Note) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.note_item_elevation))
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (note.content.isNotBlank()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
                )
            }
        }
    }
}

@Composable
fun ThemeSelector(
    currentColorScheme: ColorScheme,
    onThemeChange: (ColorScheme) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val themeOptions = ThemeManager.getThemeOptions()
    val currentThemeName = themeOptions.find { it.colorScheme == currentColorScheme }
        ?.let { stringResource(it.nameResId) } ?: stringResource(R.string.theme_blue)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        TextButton(onClick = { expanded = true }) {
            Text("${stringResource(R.string.theme_selector)}: $currentThemeName")
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            themeOptions.forEach { themeOption ->
                DropdownMenuItem(
                    text = { Text(stringResource(themeOption.nameResId)) },
                    onClick = {
                        expanded = false
                        onThemeChange(themeOption.colorScheme)
                    }
                )
            }
        }
    }
}