package com.strawhat.mymovies.vm.details.events

import com.strawhat.mymovies.vm.MovieItem

sealed class DetailsAction
data class MarkAsFavoriteAction(val movie: MovieItem) : DetailsAction()
data class UnMarkAsFavoriteAction(val id: Int) : DetailsAction()