package com.example.currency.network

import com.example.currency.domain.model.MovieDetail
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @Headers("accept: application/json")
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Header("Authorization") authorization: String,
        @Query("language") language: String="en-US",
        @Query("page") page:Int=1

    ): Response<com.example.currency.network.Response>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Header("Authorization") authorization: String
    ): Response<com.example.currency.network.Response>
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Header("Authorization") authorization: String
    ): Response<com.example.currency.network.Response>
    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int,@Query("language") language: String="en-US"): Response<DetailResponse>

}
