package com.strawhat.mymovies.ui.details

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.strawhat.mymovies.MyApplication
import com.strawhat.mymovies.R
import com.strawhat.mymovies.vm.MovieItem
import com.strawhat.mymovies.vm.details.DetailsViewModel
import com.strawhat.mymovies.vm.details.events.DetailsState
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_details.*

const val MOVIE_DETAILS_KEY = "MOVIE_DETAILS_KEY"

class DetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailsViewModel>()

    private lateinit var movie: MovieItem

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val bundle = savedInstanceState ?: intent.extras!!
        movie = bundle.getSerializable(MOVIE_DETAILS_KEY) as MovieItem
        (application as MyApplication).appComponent.inject(viewModel)
        viewModel.afterInit(movie)
        disposable.add(
            viewModel.viewStateRelay.subscribeBy(
                onNext = {
                    updateState(it)
                },
                onError = {
                    throw OnErrorNotImplementedException(it)
                }
            )
        )
        viewModel.markAsFavorite(movie)
    }

    private fun updateState(state: DetailsState) {
        test_text.text = if (state.movie.isFavorite) "favorite" else "not favorite"
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}