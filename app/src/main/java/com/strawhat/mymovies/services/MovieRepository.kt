package com.strawhat.mymovies.services

import com.strawhat.mymovies.services.bindings.Movie
import com.strawhat.mymovies.vm.events.SortMode
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable

class MovieRepository(private val apiService: ApiService) {

    fun loadPage(page: Int, sortMode: SortMode): @NonNull Observable<Pair<List<Movie>, Boolean>> {
        return if (sortMode == SortMode.POPULAR) {
            apiService.getPopularMovies(page).map { Pair(it.movies, it.page < it.totalPages) }
        } else {
            apiService.getTopRatedMovies(page).map { Pair(it.movies, it.page < it.totalPages) }
        }
    }

    fun loadFavorites(page: Int): @NonNull Observable<Pair<List<Movie>, Boolean>> {
        // TODO()
        return Observable.just(Pair(listOf(), false))
    }
}