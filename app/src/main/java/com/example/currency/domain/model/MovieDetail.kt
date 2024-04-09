package com.example.currency.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val genres: List<String>,
    val runtime: Int
)