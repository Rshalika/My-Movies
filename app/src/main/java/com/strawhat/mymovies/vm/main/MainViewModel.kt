package com.strawhat.mymovies.vm.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay


import com.strawhat.mymovies.services.MovieRepository
import com.strawhat.mymovies.vm.MovieItem
import com.strawhat.mymovies.vm.main.events.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val disposable = CompositeDisposable()

    private val viewActionsRelay: PublishRelay<ViewAction> = PublishRelay.create<ViewAction>()

    private val viewResultsRelay: PublishRelay<ViewResult> = PublishRelay.create<ViewResult>()

    private var previousState = MainViewState()

    val viewStateRelay: BehaviorRelay<MainViewState> = BehaviorRelay.create<MainViewState>()

    @Inject
    lateinit var movieRepository: MovieRepository


    fun afterInit() {
        val loadPage = ObservableTransformer<LoadPageAction, ViewResult> { event ->
            return@ObservableTransformer event.flatMap { action ->
                return@flatMap movieRepository.loadPage(action.page, action.sortMode)
                    .subscribeOn(Schedulers.io())
                    .map(fun(it: Pair<List<MovieItem>, Boolean>): ViewResult {
                        return LoadPageSuccessResult(action.page, it.first, it.second)
                    })
                    .onErrorReturn {
                        LoadPageFailResult(it)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWithItem(LoadingResult)
            }
        }

        val UI = ObservableTransformer<ViewAction, ViewResult> { event ->
            return@ObservableTransformer event.publish { shared ->
                return@publish Observable.mergeArray(
                    shared.ofType(LoadPageAction::class.java).compose(loadPage)
                )

            }
        }

        disposable.add(
            viewActionsRelay
                .compose(UI)
                .mergeWith(viewResultsRelay)
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
        loadPageRequested()
    }

    private fun reduce(state: MainViewState, result: ViewResult): MainViewState {
        return when (result) {
            is LoadPageSuccessResult -> {
                val items = state.items
                result.items.forEach {
                    if (items.contains(it).not()) {
                        items.add(it)
                    }
                }
                state.copy(
                    hasNext = result.hasNext,
                    lastPage = result.pageNumber,
                    items = items,
                    loading = false,
                    errorMessage = null
                )
            }
            is LoadPageFailResult -> {
                state.copy(
                    loading = false,
                    errorMessage = result.throwable.message
                )
            }
            LoadingResult -> {
                state.copy(loading = true)
            }
            LoadPageRequestedResult -> {
                if (state.loading.not()) {
                    viewActionsRelay.accept(LoadPageAction(state.lastPage + 1, state.sortMode))
                }
                state.copy()
            }
            is SortModeChangedResult -> {
                loadPageRequested()
                state.copy(sortMode = result.sortMode, lastPage = 0, items = linkedSetOf())
            }
        }
    }

    private fun emmit(state: MainViewState) {
        previousState = state
        viewStateRelay.accept(state)
    }

    fun loadPageRequested() {
        viewResultsRelay.accept(LoadPageRequestedResult)
    }

    fun changeSortMode(sortMode: SortMode) {
        viewResultsRelay.accept(SortModeChangedResult(sortMode))
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}