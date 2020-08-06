package com.strawhat.mymovies.vm.main.events

import com.strawhat.mymovies.vm.MovieItem


sealed class ViewResult

data class LoadPageSuccessResult(
    val pageNumber: Int,
    val items: List<MovieItem>,
    val hasNext: Boolean
) : ViewResult()

data class LoadPageFailResult(val throwable: Throwable) : ViewResult()
object NoInternetResult : ViewResult()
object LoadPageRequestedResult : ViewResult()
data class SortModeChangedResult(val sortMode: SortMode) : ViewResult()
object LoadingResult : ViewResult()