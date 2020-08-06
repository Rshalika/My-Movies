package com.strawhat.mymovies.services.bindings

import com.google.gson.annotations.SerializedName


data class MovieResponse(
    @SerializedName("page")
    var page: Int,
    @SerializedName("results")
    var movieBindings: List<MovieBinding>,
    @SerializedName("total_pages")
    var totalPages: Int,
    @SerializedName("total_results")
    var totalResults: Int
)

data class MovieBinding(
    @SerializedName("backdrop_path")
    var backdropPath: String,
    @SerializedName("release_date")
    var firstAirDate: String,
    @SerializedName("genre_ids")
    var genreIds: List<Int>,
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var name: String,
    @SerializedName("origin_country")
    var originCountry: List<String>,
    @SerializedName("original_language")
    var originalLanguage: String,
    @SerializedName("original_title")
    var originalName: String,
    @SerializedName("overview")
    var overview: String,
    @SerializedName("popularity")
    var popularity: Double,
    @SerializedName("poster_path")
    var posterPath: String,
    @SerializedName("vote_average")
    var voteAverage: Double,
    @SerializedName("vote_count")
    var voteCount: Int
)