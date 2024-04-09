package com.example.currency.ui.movie.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currency.viewmodel.repositoryimpl.MovieRepository
import com.example.currency.ui.MovieDetailState
import com.example.currency.ui.MoviesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MovieListViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _moviesState = MutableStateFlow<MoviesState>(MoviesState.Loading)
    val movies: StateFlow<MoviesState> get() = _moviesState

    private val _movieDetailState = MutableStateFlow<MovieDetailState>(MovieDetailState.Loading)
    val movieDetailState: StateFlow<MovieDetailState> get()= _movieDetailState

    fun fetchMovies(tabIndex: Int) {
        _moviesState.value = MoviesState.Loading

        viewModelScope.launch {
            try {
                val movies = when (tabIndex) {
                    0 -> repository.getNowPlayingMovies()
                    1 -> repository.getPopularMovies()
                    2 -> repository.getUpcomingMovies()
                    else -> emptyList()
                }
                _moviesState.value = MoviesState.Success(movies)
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error("Failed to fetch movies: ${e.message}")
            }
        }
    }
    fun fetchMovieDetail(movieId: Int) {
        viewModelScope.launch {
            try {
                val movieDetail = repository.getMovieDetail(movieId)
                _movieDetailState.value = MovieDetailState.Success(movieDetail)
            } catch (e: Exception) {
                _movieDetailState.value = MovieDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }    }

