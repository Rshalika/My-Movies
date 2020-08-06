package com.strawhat.mymovies.services

import com.strawhat.mymovies.services.bindings.MovieBinding
import com.strawhat.mymovies.services.db.MovieDao
import com.strawhat.mymovies.services.db.MovieEntity
import com.strawhat.mymovies.vm.MovieItem
import com.strawhat.mymovies.vm.main.events.SortMode
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

private const val DEFAULT_PAGE_SIZE = 20

class MovieRepository(private val apiService: ApiService, private val movieDao: MovieDao) {

    fun loadPage(
        page: Int,
        sortMode: SortMode
    ): @NonNull Observable<Pair<List<MovieItem>, Boolean>> {
        when (sortMode) {
            SortMode.POPULAR -> {
                return apiService.getPopularMovies(page)
                    .map {
                        Pair(
                            it.movieBindings.map { convertMovieBindingToMovieItem(it) },
                            it.page < it.totalPages
                        )
                    }
            }
            SortMode.TOP_RATED -> {
                return apiService.getTopRatedMovies(page)
                    .map {
                        Pair(
                            it.movieBindings.map { convertMovieBindingToMovieItem(it) },
                            it.page < it.totalPages
                        )
                    }
            }
            else -> {
                return loadFavorites(page)
            }
        }
    }

    private fun loadFavorites(page: Int): @NonNull Observable<Pair<List<MovieItem>, Boolean>> {
        val _page = page - 1
        return Observable.fromCallable {
            movieDao.getFavorites(DEFAULT_PAGE_SIZE, _page * DEFAULT_PAGE_SIZE)
        }.map { movies ->
            val count = movieDao.countFavorites()
            return@map Pair(
                movies.map { convertEntityToMovieItem(it) },
                count > _page * DEFAULT_PAGE_SIZE
            )
        }
            .subscribeOn(Schedulers.io())
    }

    fun markAsFavorite(movieItem: MovieItem) {
        val savedMovie = movieDao.findById(movieItem.id)
        if (savedMovie != null) {
            savedMovie.isFavorite = true
            movieDao.update(savedMovie)
        } else {
            movieDao.insert(convertMovieItemToEntity(movieItem.apply { isFavorite = true }))
        }
    }

    fun unMarkAsFavorite(id: Int) {
        movieDao.findById(id)?.let {
            movieDao.update(it.apply { isFavorite = false })
        }
    }

    private fun convertMovieBindingToMovieItem(binding: MovieBinding): MovieItem {
        return MovieItem(
            backdropPath = binding.backdropPath,
            firstAirDate = binding.firstAirDate,
            genreIds = binding.genreIds,
            id = binding.id,
            name = binding.name,
            originalName = binding.originalName,
            overview = binding.overview,
            posterPath = binding.posterPath,
            voteAverage = binding.voteAverage
        )
    }

    private fun convertMovieItemToEntity(item: MovieItem): MovieEntity {
        val genreIds = item.genreIds.joinToString(",")
        return MovieEntity(
            id = item.id,
            backdropPath = item.backdropPath,
            firstAirDate = item.firstAirDate,
            genreIds = genreIds,
            name = item.name,
            originalName = item.originalName,
            overview = item.overview,
            posterPath = item.posterPath,
            voteAverage = item.voteAverage,
            isFavorite = item.isFavorite
        )
    }

    private fun convertEntityToMovieItem(entity: MovieEntity): MovieItem {
        return MovieItem(
            id = entity.id,
            backdropPath = entity.backdropPath,
            firstAirDate = entity.firstAirDate,
            genreIds = entity.genreIds.split(",").map { it.toInt() },
            name = entity.name,
            originalName = entity.originalName,
            overview = entity.overview,
            posterPath = entity.posterPath,
            voteAverage = entity.voteAverage,
            isFavorite = entity.isFavorite
        )
    }

    fun loadMovie(id: Int): MovieItem? {
        val entity = movieDao.findById(id)
        return if (entity != null) {
            convertEntityToMovieItem(entity)
        } else {
            null
        }
    }
}