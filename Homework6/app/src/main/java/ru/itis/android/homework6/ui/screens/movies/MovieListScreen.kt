package ru.itis.android.homework6.ui.screens.movies

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ru.itis.android.homework6.R
import ru.itis.android.homework6.entity.MovieEntity
import ru.itis.android.homework6.repository.SortType
import ru.itis.android.homework6.ui.screens.navigation.Screen
import ru.itis.android.homework6.viewmodels.MovieViewModel
import ru.itis.android.homework6.viewmodels.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    var showSortSheet by remember { mutableStateOf(false) }
    val moviesState by viewModel.moviesState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    IconButton(onClick = { showSortSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = stringResource(R.string.sort))
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.Person, contentDescription = stringResource(R.string.profile))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddMovie.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_movie))
            }
        }
    ) { paddingValues ->
        when (val state = moviesState) {
            is MovieViewModel.MoviesState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MovieViewModel.MoviesState.Success -> {
                if (state.movies.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.LocalMovies,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.empty_movies_list))
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.movies) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = {}
                            )
                        }
                    }
                }
            }
            is MovieViewModel.MoviesState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.error))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMovies() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
        }

        if (showSortSheet) {
            SortBottomSheet(
                onDismiss = { showSortSheet = false },
                onSortSelected = { sortType ->
                    viewModel.setSortType(sortType)
                    showSortSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCard(
    movie: MovieEntity,
    onClick: () -> Unit
) {
    val placeholderImageUrl = stringResource(R.string.placeholder_image_url)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = movie.posterUrl ?: placeholderImageUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .size(100.dp, 150.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = movie.description.take(100) +
                            if (movie.description.length > 100)
                                stringResource(R.string.ellipsis)
                            else
                                stringResource(R.string.empty_string),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${movie.year}",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = stringResource(R.string.rating_format, movie.rating),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(R.string.duration_format, movie.duration),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Text(
                    text = movie.genre,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    onDismiss: () -> Unit,
    onSortSelected: (SortType) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.sort),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SortOption(
                text = stringResource(R.string.sort_by_date),
                onClick = { onSortSelected(SortType.DATE) }
            )

            SortOption(
                text = stringResource(R.string.sort_by_title),
                onClick = { onSortSelected(SortType.TITLE) }
            )

            SortOption(
                text = stringResource(R.string.sort_by_rating),
                onClick = { onSortSelected(SortType.RATING) }
            )

            SortOption(
                text = stringResource(R.string.sort_by_year),
                onClick = { onSortSelected(SortType.YEAR) }
            )
        }
    }
}

@Composable
fun SortOption(
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}