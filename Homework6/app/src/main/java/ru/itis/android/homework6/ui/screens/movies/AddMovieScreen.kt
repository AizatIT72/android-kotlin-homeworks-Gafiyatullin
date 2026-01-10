package ru.itis.android.homework6.ui.screens.movies

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.itis.android.homework6.R
import ru.itis.android.homework6.viewmodels.MovieViewModel
import ru.itis.android.homework6.viewmodels.ViewModelFactory

private object ErrorKeys {
    const val TITLE = "title"
    const val YEAR = "year"
    const val RATING = "rating"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    navController: NavController,
    viewModel: MovieViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var posterUrl by remember { mutableStateOf("") }

    var errors by remember { mutableStateOf(mapOf<String, String>()) }

    val titleRequired = stringResource(R.string.title_required)
    val yearRequired = stringResource(R.string.year_required)
    val yearRangeError = stringResource(R.string.year_range_error)
    val yearNumberError = stringResource(R.string.year_number_error)
    val ratingRequired = stringResource(R.string.rating_required)
    val ratingInvalid = stringResource(R.string.rating_invalid)
    val ratingNumberError = stringResource(R.string.rating_number_error)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.add_movie_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val newErrors = mutableMapOf<String, String>()

                    if (title.isEmpty()) newErrors[ErrorKeys.TITLE] = titleRequired

                    if (year.isEmpty()) {
                        newErrors[ErrorKeys.YEAR] = yearRequired
                    } else {
                        year.toIntOrNull()?.let {
                            if (it < 1900 || it > 2100) {
                                newErrors[ErrorKeys.YEAR] = yearRangeError
                            }
                        } ?: run {
                            newErrors[ErrorKeys.YEAR] = yearNumberError
                        }
                    }

                    if (rating.isEmpty()) {
                        newErrors[ErrorKeys.RATING] = ratingRequired
                    } else {
                        rating.toFloatOrNull()?.let {
                            if (it < 0 || it > 10) {
                                newErrors[ErrorKeys.RATING] = ratingInvalid
                            }
                        } ?: run {
                            newErrors[ErrorKeys.RATING] = ratingNumberError
                        }
                    }

                    errors = newErrors

                    if (newErrors.isEmpty()) {
                        viewModel.addMovie(
                            title = title,
                            description = description,
                            year = year.toInt(),
                            genre = genre,
                            duration = duration.toIntOrNull() ?: 0,
                            rating = rating.toFloat(),
                            posterUrl = if (posterUrl.isNotEmpty()) posterUrl else null
                        )
                        navController.popBackStack()
                    }
                }
            ) {
                Icon(Icons.Default.Done, contentDescription = stringResource(R.string.save))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.movie_title)) },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey(ErrorKeys.TITLE)
            )

            if (errors.containsKey(ErrorKeys.TITLE)) {
                Text(
                    text = errors[ErrorKeys.TITLE] ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.movie_description)) },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text(stringResource(R.string.movie_year)) },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    isError = errors.containsKey(ErrorKeys.YEAR)
                )

                OutlinedTextField(
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text(stringResource(R.string.movie_rating)) },
                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    isError = errors.containsKey(ErrorKeys.RATING)
                )
            }

            if (errors.containsKey(ErrorKeys.YEAR) || errors.containsKey(ErrorKeys.RATING)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (errors.containsKey(ErrorKeys.YEAR)) {
                        Text(
                            text = errors[ErrorKeys.YEAR] ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (errors.containsKey(ErrorKeys.RATING)) {
                        Text(
                            text = errors[ErrorKeys.RATING] ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text(stringResource(R.string.movie_genre)) },
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text(stringResource(R.string.movie_duration)) },
                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = posterUrl,
                onValueChange = { posterUrl = it },
                label = { Text(stringResource(R.string.movie_poster_url)) },
                leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}