package com.strawhat.mymovies.services


import com.strawhat.mymovies.BuildConfig
import com.strawhat.mymovies.services.bindings.MovieResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("3/movie/popular")
    fun getPopularMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String = "en-US"
    ): Observable<MovieResponse>

    @GET("3/movie/top_rated")
    fun getTopRatedMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("language") language: String = "en-US"
    ): Observable<MovieResponse>
}