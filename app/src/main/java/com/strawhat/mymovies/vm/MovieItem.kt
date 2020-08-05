package com.strawhat.mymovies.vm

import java.io.Serializable

data class MovieItem(

    var backdropPath: String?,

    var firstAirDate: String?,

    var genreIds: List<Int>,

    var id: Int,

    var name: String?,

    var originalName: String?,

    var overview: String?,

    var posterPath: String?,

    var voteAverage: Double,

    var isFavorite: Boolean = false
) : Serializable