package com.strawhat.mymovies.vm.details.events

sealed class DetailsResult
data class MarkAsFavoriteResult(val id: Int) : DetailsResult()
data class UnMarkAsFavoriteResult(val id: Int) : DetailsResult()