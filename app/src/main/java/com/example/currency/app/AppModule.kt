package com.example.currency.app

import com.example.currency.network.RetrofitClient
import com.example.currency.ui.movie.viewmodel.MovieListViewModel
import com.example.currency.viewmodel.repositoryimpl.MovieRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { RetrofitClient.movieApiService }

    viewModel { MovieListViewModel(get()) }

    single { MovieRepository(get()) }
}
