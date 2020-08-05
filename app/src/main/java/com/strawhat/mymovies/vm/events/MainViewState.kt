package com.strawhat.mymovies.vm.events

import com.strawhat.mymovies.services.bindings.Movie


data class MainViewState(
    val lastPage: Int = 0,
    val items: LinkedHashSet<Movie> = linkedSetOf(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val sortMode: SortMode = SortMode.POPULAR,
    val favoritesEnabled: Boolean = false,
    val hasNext: Boolean = true

)

enum class SortMode {
    POPULAR, TOP_RATED
}