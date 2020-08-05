package com.strawhat.mymovies.vm.events

import com.strawhat.mymovies.services.bindings.Movie


data class MainViewState(
    val lastPage: Int = 0,
    val items: LinkedHashSet<Movie> = linkedSetOf(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val searchEnabled: Boolean = false,
    val searchQuery: String? = null,
    val hasNext: Boolean = true

)