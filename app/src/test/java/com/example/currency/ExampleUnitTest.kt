package com.example.currency

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currency.domain.model.Movie
import com.example.currency.network.DetailResponse
import com.example.currency.network.Genre
import com.example.currency.network.MovieApiService
import com.example.currency.network.ProductionCompany
import com.example.currency.network.ProductionCountry
import com.example.currency.network.SpokenLanguage
import com.example.currency.ui.MoviesState
import com.example.currency.ui.movie.viewmodel.MovieListViewModel
import com.example.currency.viewmodel.repositoryimpl.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {

    @Mock
    private lateinit var movieApiService: MovieApiService

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: MovieRepository
    private lateinit var viewModel: MovieListViewModel

    @Before
    fun setup() {
        repository = MovieRepository(movieApiService)
        viewModel = MovieListViewModel(repository)
    }

    @Test
    fun `test fetchMovies success`() = runBlocking {
        // Mocked movies
        val mockedMovies = listOf(
            Movie(
                adult = false,
                backdrop_path = "/backdrop_path_1.jpg",
                genre_ids = listOf(1, 2),
                id = 1,
                original_language = "en",
                original_title = "Original Title 1",
                overview = "Overview 1",
                popularity = 7.8,
                poster_path = "/poster_path_1.jpg",
                release_date = "2024-01-01",
                title = "Title 1",
                video = false,
                vote_average = 8.0,
                vote_count = 100
            ),
            Movie(
                adult = false,
                backdrop_path = "/backdrop_path_2.jpg",
                genre_ids = listOf(3, 4),
                id = 2,
                original_language = "en",
                original_title = "Original Title 2",
                overview = "Overview 2",
                popularity = 8.2,
                poster_path = "/poster_path_2.jpg",
                release_date = "2024-02-01",
                title = "Title 2",
                video = false,
                vote_average = 8.5,
                vote_count = 150
            ),
            Movie(
                adult = false,
                backdrop_path = "/backdrop_path_3.jpg",
                genre_ids = listOf(5, 6),
                id = 3,
                original_language = "en",
                original_title = "Original Title 3",
                overview = "Overview 3",
                popularity = 6.5,
                poster_path = "/poster_path_3.jpg",
                release_date = "2024-03-01",
                title = "Title 3",
                video = false,
                vote_average = 7.0,
                vote_count = 80
            )
        )

        `when`(movieApiService.getNowPlayingMovies(any())).thenReturn(Response.success(com.example.currency.network.Response(mockedMovies)))
        val moviesState = viewModel.movies.value
        assertEquals(moviesState, MoviesState.Success(mockedMovies))
    }

    @Test
    fun `test fetchMovies error`() = runBlocking {
        `when`(movieApiService.getNowPlayingMovies(any())).thenThrow(Exception("Network error"))

        viewModel.fetchMovies(0)

        val moviesState = viewModel.movies.value
        assertEquals(moviesState, MoviesState.Error("Failed to fetch movies: Network error"))
    }




}