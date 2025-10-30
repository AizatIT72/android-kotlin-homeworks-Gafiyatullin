package ru.itis.android.homework3.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.itis.android.homework3.R
import ru.itis.android.homework3.model.Note
import ru.itis.android.homework3.util.dimensionResource
import ru.itis.android.homework3.util.stringResource
import ru.itis.android.homework3.viewmodel.NotesViewModel
import ru.itis.android.homework3.constants.AppConstants

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    val notesViewModel: NotesViewModel = viewModel(
        viewModelStoreOwner = navController.getBackStackEntry(AppConstants.Navigation.NOTES_SCREEN)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_large))
    ) {
        Text(
            text = stringResource(R.string.new_note_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_large))
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                titleError = false
            },
            label = { Text(stringResource(R.string.note_title_hint)) },
            modifier = Modifier.fillMaxWidth(),
            isError = titleError,
            supportingText = {
                if (titleError) {
                    Text(stringResource(R.string.note_title_empty_error))
                }
            }
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text(stringResource(R.string.note_content_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.text_field_height)),
            singleLine = false
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (title.isBlank()) {
                    titleError = true
                } else {
                    notesViewModel.addNote(Note(title.trim(), content.trim()))
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_button))
        }
    }
}