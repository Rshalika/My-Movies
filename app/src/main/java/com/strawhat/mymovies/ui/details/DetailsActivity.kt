package com.strawhat.mymovies.ui.details

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.strawhat.mymovies.BuildConfig
import com.strawhat.mymovies.MyApplication
import com.strawhat.mymovies.R
import com.strawhat.mymovies.ui.main.MainActivity
import com.strawhat.mymovies.util.GenresUtil
import com.strawhat.mymovies.vm.MovieItem
import com.strawhat.mymovies.vm.details.DetailsViewModel
import com.strawhat.mymovies.vm.details.events.DetailsState
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.content_details.*
import java.text.SimpleDateFormat
import java.util.*

const val MOVIE_DETAILS_KEY = "MOVIE_DETAILS_KEY"

class DetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailsViewModel>()

    private lateinit var movie: MovieItem

    private val disposable = CompositeDisposable()

    private var toolbarCollapsed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(findViewById(R.id.detail_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
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
        Glide
            .with(this)
            .load("${BuildConfig.IMAGES_URL_PREFIX}${movie.backdropPath}")
            .centerCrop()
            .into(object : CustomViewTarget<ImageView, Drawable>(movie_image) {
                override fun onLoadFailed(errorDrawable: Drawable?) {

                }

                override fun onResourceCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    movie_image.setImageDrawable(resource)
                    val toBitmap = resource.toBitmap()
                    val builder = Palette.Builder(toBitmap)
                    val palette = builder.generate()

                    val dominantSwatch = palette.dominantSwatch
                    val vibrantSwatch = palette.vibrantSwatch
                    val lightVibrantSwatch = palette.lightVibrantSwatch
                    val lightMutedSwatch = palette.lightMutedSwatch
                    val darkVibrantSwatch = palette.darkVibrantSwatch
                    val darkMutedSwatch = palette.darkMutedSwatch
                    val secondaryLightColor =
                        resources.getColor(R.color.secondaryLightColor, theme)
                    val _primaryColor =
                        resources.getColor(R.color.primaryColor, theme)
                    val primaryDarkColor =
                        resources.getColor(R.color.primaryDarkColor, theme)

                    val lightColor = arrayListOf(
                        lightVibrantSwatch?.rgb,
                        lightMutedSwatch?.rgb,
                        secondaryLightColor
                    ).find { it != null }!!

                    val primaryColor = arrayListOf(
                        vibrantSwatch?.rgb,
                        dominantSwatch?.rgb,
                        _primaryColor
                    ).find { it != null }!!

                    val darkColor = arrayListOf(
                        darkVibrantSwatch?.rgb,
                        darkMutedSwatch?.rgb,
                        primaryDarkColor
                    ).find { it != null }!!

                    (findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout))?.setContentScrimColor(
                        primaryColor
                    )

                    val window: Window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = darkColor

                    rating_bar.backgroundTintList = ColorStateList.valueOf(lightColor)
                    details_rating.setTextColor(lightColor)
                }

            })

        description.text = movie.overview
        details_rating.text = movie.voteAverage.toString()
        details_title.text = movie.name
        movie.originalName?.let {
            original_title.text = resources.getString(R.string.original_title, movie.originalName)
        }
        try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(movie.firstAirDate!!)?.let {
                release_date.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it)
            }
        } catch (e: Exception) {

        }

        movie.genreIds.forEach {
            val genre = GenresUtil.genreMap[it]?.let {
                val textView =
                    LayoutInflater.from(this)
                        .inflate(R.layout.genre_name_text_view, categories_list, false) as TextView
                textView.text = it
                categories_list.addView(textView)
            }

        }
        rating_bar.rating = movie.voteAverage.div(2).toFloat()

        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            handleToolbarStateChange(appBarLayout.height / 2 < -verticalOffset)
        })
        favorite_button.setOnClickListener {
            toggleFavorite()
        }
        viewModel.loadMovie(movie)
    }

    private fun toggleFavorite() {
        if (movie.isFavorite) {
            viewModel.unMarkAsFavorite(movie.id)
        } else {
            viewModel.markAsFavorite(movie)
        }
    }

    private fun handleToolbarStateChange(collapsed: Boolean) {
        if (toolbarCollapsed != collapsed) {
            toolbarCollapsed = collapsed
            if (collapsed) {
                favorite_button.visibility = View.GONE
            } else {
                favorite_button.visibility = View.VISIBLE
            }
            invalidateOptionsMenu()
        }
    }

    private fun updateState(state: DetailsState) {
        this.movie = state.movie
        if (movie.isFavorite) {
            favorite_button.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            favorite_button.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
        invalidateOptionsMenu()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(0)?.let { item ->
            item.isVisible = toolbarCollapsed
            if (movie.isFavorite) {
                item.setIcon(R.drawable.ic_baseline_favorite_24)
            } else {
                item.setIcon(R.drawable.ic_baseline_favorite_border_24)
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, MainActivity::class.java))

                true
            }
            R.id.action_favorite -> {
                toggleFavorite()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}