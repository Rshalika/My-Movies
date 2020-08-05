package com.strawhat.mymovies.vm.events

import com.strawhat.mymovies.services.bindings.Movie


sealed class ViewResult

data class LoadPageSuccessResult(
    val pageNumber: Int,
    val items: List<Movie>,
    val hasNext: Boolean
) : ViewResult()

data class LoadPageFailResult(val throwable: Throwable) : ViewResult()
object LoadPageRequestedResult : ViewResult()
object FavoritesRequestResult : ViewResult()
data class SortModeChangedResult(val sortMode: SortMode) : ViewResult()
object LoadingResult : ViewResult()
object FavoritesActivatedResult : ViewResult()
object FavoritesDeActivatedResult : ViewResult()
data class FavoritesSuccessResult(
    val pageNumber: Int,
    val items: List<Movie>,
    val hasNext: Boolean
) :
    ViewResult()

data class FavoritesPageFailResult(val throwable: Throwable) : ViewResult()