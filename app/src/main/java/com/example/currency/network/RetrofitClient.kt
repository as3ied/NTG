package com.example.currency.network

import com.example.currency.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val apiKey = BuildConfig.API_KEY // Assuming your API key is in BuildConfig
    val authorizationHeader = "Bearer $apiKey"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .addHeader("Authorization", authorizationHeader)

            val newRequest = requestBuilder.build()
             chain.proceed(newRequest)
        }
        .build()


    private fun provideMovieService(): MovieApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(MovieApiService::class.java)
    }

    val movieApiService = provideMovieService()
}