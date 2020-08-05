package com.strawhat.mymovies.services.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieEntity(

    @PrimaryKey
    var id: Int,

    @ColumnInfo(name = "backdrop_path")
    var backdropPath: String?,

    @ColumnInfo(name = "first_air_date")
    var firstAirDate: String?,

    @ColumnInfo(name = "genre_ids")
    var genreIds: String,

    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "original_name")
    var originalName: String?,

    @ColumnInfo(name = "overview")
    var overview: String?,

    @ColumnInfo(name = "poster_path")
    var posterPath: String?,

    @ColumnInfo(name = "vote_average")
    var voteAverage: Double,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false
)