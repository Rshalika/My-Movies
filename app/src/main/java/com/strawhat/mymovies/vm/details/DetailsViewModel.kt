package com.strawhat.mymovies.vm.details

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay
import com.strawhat.mymovies.services.MovieRepository
import com.strawhat.mymovies.vm.MovieItem
import com.strawhat.mymovies.vm.details.events.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class DetailsViewModel : ViewModel() {

    private val disposable = CompositeDisposable()

    private val viewActionsRelay = PublishRelay.create<DetailsAction>()

    val viewStateRelay = BehaviorRelay.create<DetailsState>()

    private lateinit var previousState: DetailsState

    @Inject
    lateinit var repository: MovieRepository

    fun afterInit(movieDetails: MovieItem) {
        previousState = DetailsState(movieDetails)
        val markAsFavorite = ObservableTransformer<MarkAsFavoriteAction, DetailsResult> { event ->
            return@ObservableTransformer event.flatMap { action ->
                Observable.fromCallable {
                    repository.markAsFavorite(action.movie)
                }
                    .subscribeOn(Schedulers.io())
                    .map {
                        MarkAsFavoriteResult(action.movie.id)
                    }
            }

        }
        val unMarkAsFavorite =
            ObservableTransformer<UnMarkAsFavoriteAction, DetailsResult> { event ->
                return@ObservableTransformer event.flatMap { action ->
                    Observable.fromCallable {
                        repository.unMarkAsFavorite(action.id)
                    }
                        .subscribeOn(Schedulers.io())
                        .map {
                            UnMarkAsFavoriteResult(action.id)
                        }
                }
            }
        val loadMovie =
            ObservableTransformer<LoadMovieAction, DetailsResult> { event ->
                return@ObservableTransformer event.flatMap { action ->
                    Observable.fromCallable {
                        repository.loadMovie(action.id) ?: Unit
                    }
                        .subscribeOn(Schedulers.io())
                        .map {
                            if (it is Unit) {
                                return@map LoadMovieResult(null)
                            } else {
                                return@map LoadMovieResult(it as MovieItem)
                            }

                        }
                }
            }

        val UI = ObservableTransformer<DetailsAction, DetailsResult> { event ->
            return@ObservableTransformer event.publish { shared ->
                return@publish Observable.mergeArray(
                    shared.ofType(MarkAsFavoriteAction::class.java).compose(markAsFavorite),
                    shared.ofType(UnMarkAsFavoriteAction::class.java).compose(unMarkAsFavorite),
                    shared.ofType(LoadMovieAction::class.java).compose(loadMovie)
                )
            }
        }
        disposable.add(
            viewActionsRelay
                .compose(UI)
                .observeOn(AndroidSchedulers.mainThread())
                .scan(previousState, { state, result ->
                    return@scan reduce(state, result)
                })
                .subscribeBy(
                    onNext = {
                        emmit(it)
                    },
                    onError = {
                        throw OnErrorNotImplementedException(it)
                    }
                )
        )
    }

    private fun emmit(state: DetailsState) {
        previousState = state
        viewStateRelay.accept(state)
    }

    private fun reduce(state: DetailsState, result: DetailsResult): DetailsState {
        return when (result) {
            is MarkAsFavoriteResult -> state.copy(movie = state.movie.copy(isFavorite = true))
            is UnMarkAsFavoriteResult -> state.copy(movie = state.movie.copy(isFavorite = false))
            is LoadMovieResult -> if (result.movie != null) state.copy(movie = result.movie) else state.copy()
        }
    }

    fun markAsFavorite(movie: MovieItem) {
        viewActionsRelay.accept(MarkAsFavoriteAction(movie))
    }

    fun unMarkAsFavorite(id: Int) {
        viewActionsRelay.accept(UnMarkAsFavoriteAction(id))
    }


    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun loadMovie(movie: MovieItem) {
        viewActionsRelay.accept(LoadMovieAction(movie.id))
    }
}