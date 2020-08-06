package com.strawhat.mymovies.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.strawhat.mymovies.BuildConfig
import com.strawhat.mymovies.R
import com.strawhat.mymovies.vm.MovieItem

class MovieListAdapter(
    private val parentActivity: MainActivity,
    val onClickListener: (MovieItem) -> Unit
) :
    RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    private val mDiffer: AsyncListDiffer<MovieItem> = AsyncListDiffer<MovieItem>(
        this,
        DIFF_CALLBACK
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mDiffer.currentList[position]
        Glide
            .with(parentActivity)
            .load("${BuildConfig.IMAGES_URL_PREFIX}${item.posterPath}")
            .centerCrop()
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            onClickListener(item)
        }
    }

    fun setMovies(newList: List<MovieItem>) {
        mDiffer.submitList(newList)
    }

    override fun getItemCount() = mDiffer.currentList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.list_item_image)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieItem>() {
            override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}