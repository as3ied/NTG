package com.example.currency.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.currency.viewmodel.repositoryimpl.MovieRepository
import com.example.currency.domain.model.Movie
import com.example.currency.network.DetailResponse
import com.example.currency.network.RetrofitClient
import com.example.currency.ui.movie.viewmodel.MovieListViewModel

class MainActivity : AppCompatActivity(), ViewModelProvider.Factory {

    private lateinit var viewModel: MovieListViewModel
    private lateinit var movieRepository: MovieRepository
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieApiService = RetrofitClient.movieApiService
        movieRepository = MovieRepository(movieApiService)
        val factory = MovieListViewModelFactory(movieRepository)
        viewModel = ViewModelProvider(this, factory).get(MovieListViewModel::class.java)
        viewModel.fetchMovies(0)
        setContent {
            val navController = rememberNavController()
            this.navController = navController // Save the navController instance
            NavHost(navController, startDestination = "movie_list") {
                composable("movie_list") {
                    MainScreen(navController)
                }
                composable("movie_detail/{movieId}") { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
                    MovieDetailScreen(viewModel, movieId,navController)
                }
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainScreen(navController: NavHostController) {
        var moviesState = remember { mutableStateOf<MoviesState>(MoviesState.Loading) }
        LaunchedEffect(key1 = viewModel) {
            viewModel.movies.collect { state ->
                moviesState.value = state
            }
        }

        Scaffold(
            content = {
                MovieListScreen(viewModel, moviesState.value, navController)
            }
        )
    }

    @Composable
    fun MovieListScreen(
        viewModel: MovieListViewModel,
        moviesState: MoviesState,
        navController: NavHostController
    ) {
        val tabs = listOf("Now Playing", "Popular", "Upcoming")
        var selectedTabIndex = remember { mutableStateOf(0) }

        Column {
            TabRow(selectedTabIndex.value) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        onClick = {
                            selectedTabIndex.value = index
                            viewModel.fetchMovies(index)
                        },
                        text = { Text(title) }
                    )
                }
            }
            when (moviesState) {
                is MoviesState.Loading -> Text("Loading...")
                is MoviesState.Success -> MovieList(moviesState.movies, navController)
                is MoviesState.Error -> Text("Error: ${moviesState.message}")
            }
        }
    }

    @Composable
    fun MovieList(movies: List<Movie>, navController: NavHostController) {
        LazyRow {
            items(movies.size) { i ->
                val movie = movies[i]
                MovieItem(movie = movie, onItemClick = {
                    navController.navigate("movie_detail/${it.id}")
                })
            }
        }
    }

    @Composable
    fun MovieDetailScreen(viewModel: MovieListViewModel, movieId: Int, navController: NavHostController) {
        var movieDetailState = remember { mutableStateOf<MovieDetailState>(MovieDetailState.Loading) }

        LaunchedEffect(movieId) {
            viewModel.fetchMovieDetail(movieId)
            viewModel.movieDetailState.collect { state ->
                movieDetailState.value = state
            }
        }

        when (val state = movieDetailState.value) {
            is MovieDetailState.Loading -> {
                Text(text = "Loading...")
            }

            is MovieDetailState.Success -> {
                val movie = state.movie
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = rememberImagePainter(
                                data = movie?.baseUrl + movie?.poster_path,
                                builder = {
                                    crossfade(true)
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentScale = ContentScale.FillBounds
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = movie?.title ?: "empty title",
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = movie?.overview ?: "empty description",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            }

            is MovieDetailState.Error -> {
                Text(text = "Error: ${state.message}")
            }
        }
    }


    @Composable
    fun MovieItem(movie: Movie, onItemClick: (Movie) -> Unit) {
        Card(
            modifier = Modifier
                .padding(top = 10.dp, start = 8.dp)
                .clickable { onItemClick(movie) }
                .width(350.dp)
                .height(400.dp),
            backgroundColor = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                Image(
                    painter = rememberImagePainter(
                        data = movie.baseUrl + movie.poster_path,
                        builder = {
                            crossfade(true)
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .height(330.dp)
                        .fillMaxWidth() // Ensure the image takes full width
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.h6,
                    )
                }
            }
        }
    }



}

class MovieListViewModelFactory(private val repository: MovieRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieListViewModel::class.java)) {
            return MovieListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class MovieDetailState {
    object Loading : MovieDetailState()
    data class Success(val movie: DetailResponse?) : MovieDetailState()
    data class Error(val message: String) : MovieDetailState()
}

sealed class MoviesState {
    object Loading : MoviesState()
    data class Success(val movies: List<Movie>) : MoviesState()
    data class Error(val message: String) : MoviesState()
}
