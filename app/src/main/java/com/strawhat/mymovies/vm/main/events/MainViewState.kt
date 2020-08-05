package com.strawhat.mymovies.vm.main.events

import com.strawhat.mymovies.vm.MovieItem


data class MainViewState(
    val lastPage: Int = 0,
    val items: LinkedHashSet<MovieItem> = linkedSetOf(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val sortMode: SortMode = SortMode.POPULAR,
    val hasNext: Boolean = true

)

enum class SortMode {
    POPULAR, TOP_RATED, FAVORITES
}