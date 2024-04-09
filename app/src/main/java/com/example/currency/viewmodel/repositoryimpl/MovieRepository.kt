package com.example.currency.viewmodel.repositoryimpl

import android.util.Log
import com.example.currency.BuildConfig
import com.example.currency.domain.model.Movie
import com.example.currency.network.DetailResponse
import com.example.currency.network.MovieApiService
import retrofit2.HttpException

class MovieRepository(private val movieApiService: MovieApiService) {
    private val apiKey = BuildConfig.API_KEY
    private val imgBaseUrl = "https://media.themoviedb.org/t/p/w440_and_h660_face"
    private val authorizationHeader = "Bearer $apiKey"

    suspend fun getNowPlayingMovies(): List<Movie> {
        return try {
            val response = movieApiService.getNowPlayingMovies(authorization = authorizationHeader)
            if (response.isSuccessful) {
                response.body()?.movies?.forEach{it.baseUrl=imgBaseUrl}
                response.body()?.movies ?: emptyList()
            } else {
                Log.e("MovieRepository", "Error fetching now playing movies: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("MovieRepository", "Network error fetching now playing movies", e)
            emptyList()
        }
    }
 suspend fun getPopularMovies(): List<Movie> {
     return try {
         val response = movieApiService.getPopularMovies(authorization = authorizationHeader)
         if (response.isSuccessful) {
             response.body()?.movies?.forEach{it.baseUrl=imgBaseUrl}
             response.body()?.movies ?: emptyList()
         } else {
             Log.e("MovieRepository", "Error fetching now playing movies: ${response.code()}")
             emptyList()
         }
     } catch (e: Exception) {
         Log.e("MovieRepository", "Network error fetching now playing movies", e)
         emptyList()
     }
    }
 suspend fun getUpcomingMovies(): List<Movie> {
     return try {
         val response = movieApiService.getUpcomingMovies(authorization = authorizationHeader)
         if (response.isSuccessful) {
             response.body()?.movies?.forEach{it.baseUrl=imgBaseUrl}
             response.body()?.movies ?: emptyList()
         } else {
             Log.e("MovieRepository", "Error fetching now playing movies: ${response.code()}")
             emptyList()
         }
     } catch (e: Exception) {
         Log.e("MovieRepository", "Network error fetching now playing movies", e)
         emptyList()
     }
    }


    suspend fun getMovieDetail(movieId: Int): DetailResponse {
        try {
            val response = movieApiService.getMovieDetail(movieId)
            if (response.isSuccessful) {
                response.body()?.baseUrl=imgBaseUrl
                return response.body() ?: throw Exception("No data received")
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw Exception("Error fetching movie detail: ${e.message}")
        }
    }}