package com.example.currency.network

import com.example.currency.domain.model.Movie
import com.google.gson.annotations.SerializedName

data class Response(@SerializedName("results") val movies: List<Movie>)

