package ru.itis.android.homework6.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.itis.android.homework6.R
import ru.itis.android.homework6.entity.MovieEntity
import ru.itis.android.homework6.repository.MovieRepository
import ru.itis.android.homework6.repository.SortType
import ru.itis.android.homework6.utils.SessionManager
import java.util.*

class MovieViewModel(
    application: Application,
    private val repository: MovieRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _moviesState = MutableStateFlow<MoviesState>(MoviesState.Loading)
    val moviesState: StateFlow<MoviesState> = _moviesState

    private val _sortType = MutableStateFlow(SortType.DATE)

    init {
        viewModelScope.launch {
            try {
                delay(300)
                val userId = sessionManager.getCurrentUserId()
                if (userId != null) {
                    val count = repository.getMovieCount(userId)
                    if (count == 0) {
                        addTestMovies(userId)
                    }
                }
                loadMoviesInternal()
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error(getApplication<Application>().getString(R.string.movies_load_error))
            }
        }
    }

    private suspend fun addTestMovies(userId: String) = withContext(Dispatchers.IO) {
        val testMovies = TestMoviesData.getTestMovies(userId)
        testMovies.forEach { movie ->
            repository.addMovie(movie)
        }
    }

    private fun loadMoviesInternal() {
        val userId = sessionManager.getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                repository.getMovies(userId, _sortType.value).collectLatest { movies ->
                    _moviesState.value = MoviesState.Success(movies)
                }
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error(getApplication<Application>().getString(R.string.movies_load_error))
            }
        }
    }

    fun loadMovies() {
        loadMoviesInternal()
    }

    fun setSortType(sortType: SortType) {
        _sortType.value = sortType
        loadMovies()
    }

    fun addMovie(
        title: String,
        description: String,
        year: Int,
        genre: String,
        duration: Int,
        rating: Float,
        posterUrl: String?
    ) {
        val userId = sessionManager.getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                val movie = MovieEntity(
                    title = title,
                    description = description,
                    year = year,
                    genre = genre,
                    duration = duration,
                    rating = rating,
                    posterUrl = posterUrl,
                    addedByUserId = userId,
                    createdAt = Date()
                )

                repository.addMovie(movie)
            } catch (e: Exception) {
            }
        }
    }

    fun deleteMovie(movieId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteMovie(movieId)
            } catch (e: Exception) {
            }
        }
    }

    sealed class MoviesState {
        object Loading : MoviesState()
        data class Success(val movies: List<MovieEntity>) : MoviesState()
        data class Error(val message: String) : MoviesState()
    }
}

// Тестовые данные для фильмов
private object TestMoviesData {
    fun getTestMovies(userId: String): List<MovieEntity> {
        return listOf(
            MovieEntity(
                title = "Интерстеллар",
                description = "Фантастический эпос о путешествиях сквозь пространство-время. Когда засуха приводит человечество к продовольственному кризису, коллектив исследователей и учёных отправляется сквозь червоточину в путешествие, чтобы превзойти прежние ограничения для космических путешествий человека и переселить человечество на другую планету.",
                year = 2014,
                genre = "Фантастика, Драма",
                duration = 169,
                rating = 8.6f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGEyLWFmMjktY2FiMmZkNWIyODZiXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 30), // 30 дней назад
                isFavorite = true
            ),
            MovieEntity(
                title = "Начало",
                description = "Кобб — талантливый вор, лучший из лучших в опасном искусстве извлечения: он крадет ценные секреты из глубин подсознания во время сна, когда человеческий разум наиболее уязвим.",
                year = 2010,
                genre = "Фантастика, Боевик",
                duration = 148,
                rating = 8.8f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 25), // 25 дней назад
                isFavorite = false
            ),
            MovieEntity(
                title = "Зеленая миля",
                description = "Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора», каждый из узников которого однажды проходит «зеленую милю» по пути к месту казни.",
                year = 1999,
                genre = "Фэнтези, Драма",
                duration = 189,
                rating = 9.1f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BMTUxMzQyNjA5MF5BMl5BanBnXkFtZTYwOTU2NTY3._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 20), // 20 дней назад
                isFavorite = true
            ),
            MovieEntity(
                title = "Побег из Шоушенка",
                description = "Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены и её любовника. Оказавшись в тюрьме под названием Шоушенк, он сталкивается с жестокостью и беззаконием, царящими по обе стороны решётки.",
                year = 1994,
                genre = "Драма",
                duration = 142,
                rating = 9.3f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BNDE3ODcxYzMtY2YzZC00NmNlLWJiNDMtZDViZWM2MzIxZDYwXkEyXkFqcGdeQXVyNjAwNDUxODI@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 15), // 15 дней назад
                isFavorite = false
            ),
            MovieEntity(
                title = "Властелин колец: Возвращение короля",
                description = "Повелитель сил Тьмы Саурон направляет свою бесчисленную армию под стены Минас-Тирита, крепости Последней Надежды. Он предвкушает близкую победу, но именно это мешает ему заметить две крохотные фигурки — хоббитов, приближающихся к Роковой Горе, где им предстоит уничтожить Кольцо Всевластья.",
                year = 2003,
                genre = "Фэнтези, Приключения",
                duration = 201,
                rating = 9.0f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BNzA5ZDNlZWMtM2NhNS00NDJjLTk4NDItYTRmY2EwMWZlMTY3XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 10), // 10 дней назад
                isFavorite = true
            ),
            MovieEntity(
                title = "Крестный отец",
                description = "Криминальная сага, повествующая о нью-йоркской сицилийской мафиозной семье Корлеоне. Фильм охватывает период 1945-1955 годов.",
                year = 1972,
                genre = "Криминал, Драма",
                duration = 175,
                rating = 9.2f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 5), // 5 дней назад
                isFavorite = false
            ),
            MovieEntity(
                title = "Темный рыцарь",
                description = "Когда в городе Готэм появляется Джокер, Бэтмен сталкивается с самым опасным преступником, который бросает вызов его физическим силам и решимости бороться с несправедливостью.",
                year = 2008,
                genre = "Боевик, Криминал",
                duration = 152,
                rating = 9.0f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 4), // 4 дня назад
                isFavorite = true
            ),
            MovieEntity(
                title = "Форрест Гамп",
                description = "Сидя на автобусной остановке, Форрест Гамп — не очень умный, но добрый и открытый парень — рассказывает случайным встречным историю своей необыкновенной жизни.",
                year = 1994,
                genre = "Драма, Романтика",
                duration = 142,
                rating = 8.8f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BNWIwODRlZTUtY2U3ZS00Yzg1LWJhNzYtMmZiYmEyNmU1NjMzXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3), // 3 дня назад
                isFavorite = false
            ),
            MovieEntity(
                title = "Бойцовский клуб",
                description = "Сотрудник страховой компании страдает хронической бессонницей и отчаянно пытается вырваться из скучной жизни. Встреча с продавцом мыла Тайлером Дёрденом меняет его жизнь.",
                year = 1999,
                genre = "Драма",
                duration = 139,
                rating = 8.8f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BNDIzNDU0YzEtYzE5Ni00ZjlkLTk5ZjgtNjM3NWE4YzA3Nzk3XkEyXkFqcGdeQXVyMjUzOTY1NTc@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2), // 2 дня назад
                isFavorite = true
            ),
            MovieEntity(
                title = "Матрица",
                description = "Хакер Нео узнает, что его мир — виртуальная реальность, созданная разумными машинами, чтобы подчинить и усмирить человечество.",
                year = 1999,
                genre = "Фантастика, Боевик",
                duration = 136,
                rating = 8.7f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 1), // 1 день назад
                isFavorite = false
            ),
            MovieEntity(
                title = "Паразиты",
                description = "Бедная семья внедряется в богатый дом, выдавая себя за высококвалифицированных специалистов.",
                year = 2019,
                genre = "Драма, Комедия",
                duration = 132,
                rating = 8.6f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BYWZjMjk3ZTItODQ2ZC00NTY5LWE0ZDYtZTI3MjcwN2Q5NTVkXkEyXkFqcGdeQXVyODk4OTc3MTY@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 12), // 12 часов назад
                isFavorite = true
            ),
            MovieEntity(
                title = "Леон",
                description = "Профессиональный убийца Леон неожиданно для себя самого решает помочь 12-летней соседке Матильде, чья семья погибает от рук коррумпированного полицейского.",
                year = 1994,
                genre = "Криминал, Драма",
                duration = 110,
                rating = 8.5f,
                posterUrl = "https://s1.afisha.ru/mediastorage/9d/61/2673a5cb305147939a84b87c619d.jpg",
                addedByUserId = userId,
                createdAt = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 6), // 6 часов назад
                isFavorite = false
            ),
            MovieEntity(
                title = "Джокер",
                description = "История становления Артура Флека, неудачливого комика, который встает на путь преступности и хаоса в Готэм**Сити**.",
                year = 2019,
                genre = "Драма, Криминал",
                duration = 122,
                rating = 8.4f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BNGVjNWI4ZGUtNzE0MS00YTJmLWE0ZDctN2ZiYTk2YmI3NTYyXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(),
                isFavorite = true
            ),
            MovieEntity(
                title = "Однажды в Голливуде",
                description = "История актера и его дублера, пытающихся найти свое место в быстро меняющемся мире Голливуда 1969 года.",
                year = 2019,
                genre = "Драма, Комедия",
                duration = 161,
                rating = 7.6f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BOTg4ZTNkZmUtMzNlZi00YmFjLTk1MmUtNWQwNTM0YjcyNTNkXkEyXkFqcGdeQXVyNjg2NjQwMDQ@._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(),
                isFavorite = false
            ),
            MovieEntity(
                title = "Довод",
                description = "Агент ЦРУ отправляется в опасное путешествие во времени, чтобы предотвратить начало Третьей мировой войны.",
                year = 2020,
                genre = "Фантастика, Боевик",
                duration = 150,
                rating = 7.3f,
                posterUrl = "https://m.media-amazon.com/images/M/MV5BYzg0NGM2NjAtNmIxOC00MDJmLTg5ZmYtYzM0MTE4NWE2NzlhXkEyXkFqcGdeQXVyMTA4NjE0NjEy._V1_.jpg",
                addedByUserId = userId,
                createdAt = Date(),
                isFavorite = true
            )
        )
    }
}