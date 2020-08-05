package com.strawhat.mymovies.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.strawhat.mymovies.MyApplication
import com.strawhat.mymovies.R
import com.strawhat.mymovies.vm.MainViewModel
import com.strawhat.mymovies.vm.events.MainViewState
import com.strawhat.mymovies.vm.events.SortMode
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.android.synthetic.main.content_main.*


private const val VISIBLE_THRESHOLD = 2

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var layoutManager: GridLayoutManager

    private lateinit var adapter: MovieListAdapter

    private val disposable = CompositeDisposable()

    private var initialSpinnerSelection = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        (application as MyApplication).appComponent.inject(viewModel)
        viewModel.afterInit()
        setupRecyclerView(findViewById(R.id.item_list))
        setUpLoadMoreListener()
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
        sort_mode_chooser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (initialSpinnerSelection) {
                    initialSpinnerSelection = false
                    return
                } else {
                    when (position) {
                        0 -> {
                            viewModel.changeSortMode(SortMode.POPULAR)
                        }
                        1 -> {
                            viewModel.changeSortMode(SortMode.TOP_RATED)
                        }
                        2 -> {
                            viewModel.favoritesActivated()
                        }
                    }
                }
            }
        }

    }

    private fun updateState(state: MainViewState) {
        adapter.setMovies(state.items.toMutableList())

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        adapter = MovieListAdapter(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }


    private fun setUpLoadMoreListener() {
        item_list.setOnScrollChangeListener { _, _, _, _, _ ->
            val findLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            val totalItemCount = layoutManager.itemCount
            if (totalItemCount <= findLastVisibleItemPosition + VISIBLE_THRESHOLD) {
                viewModel.loadPageRequested()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}