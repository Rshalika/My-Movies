package com.strawhat.mymovies.vm.details.events

import com.strawhat.mymovies.vm.MovieItem

sealed class DetailsResult
data class MarkAsFavoriteResult(val id: Int) : DetailsResult()
data class UnMarkAsFavoriteResult(val id: Int) : DetailsResult()
data class LoadMovieResult(val movie: MovieItem?) : DetailsResult()