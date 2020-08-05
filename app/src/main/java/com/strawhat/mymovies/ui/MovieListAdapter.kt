package com.strawhat.mymovies.ui

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
import com.strawhat.mymovies.services.bindings.Movie


class MovieListAdapter(private val parentActivity: MainActivity) :
    RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    private val mDiffer: AsyncListDiffer<Movie> = AsyncListDiffer<Movie>(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mDiffer.currentList[position]
//        holder.titleView.text = item.name
//        holder.ratingView.text = item.voteAverage.toString()
//        try {
//            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(item.firstAirDate)?.let {
//                val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
//                cal.time = it
//                val year = cal[Calendar.YEAR]
//                holder.yarView.text = year.toString()
//            }
//        } catch (e: Exception) {
//
//        }


        Glide
            .with(parentActivity)
            .load("${BuildConfig.IMAGES_URL_PREFIX}${item.posterPath}")
            .centerCrop()
//            .placeholder(R.drawable.loader_image)
            .into(holder.imageView)

        with(holder.itemView) {
            tag = item
        }
    }

    fun setMovies(newList: List<Movie>) {
        mDiffer.submitList(newList)
    }

    override fun getItemCount() = mDiffer.currentList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //        val titleView: TextView = view.findViewById(R.id.title)
        val imageView: ImageView = view.findViewById(R.id.list_item_image)
//        val ratingView: TextView = view.findViewById(R.id.rating)
//        val yarView: TextView = view.findViewById(R.id.year)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }

        }
    }

}