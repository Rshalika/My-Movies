package com.strawhat.mymovies.di


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.strawhat.mymovies.BuildConfig
import com.strawhat.mymovies.services.ApiService
import com.strawhat.mymovies.services.MovieRepository
import com.strawhat.mymovies.services.db.AppDatabase
import com.strawhat.mymovies.services.db.MovieDao
import com.strawhat.mymovies.services.system.CustomNetworkCallBack
import com.strawhat.mymovies.services.system.SystemInfoService
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApplicationModule(val application: Application) {


    @Provides
    @Singleton
    fun providesHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(apiService: ApiService, movieDao: MovieDao): MovieRepository {
        return MovieRepository(apiService, movieDao)
    }

    @Provides
    @Singleton
    fun provideMovieDao(): MovieDao {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java, "movie_database"
        ).build().movieDao()
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(): ConnectivityManager {
        return application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideSystemInfoService(): SystemInfoService {
        return SystemInfoService(application)
    }

    @Provides
    @Singleton
    fun provideNetworkCallBack(systemInfoService: SystemInfoService): CustomNetworkCallBack {
        return CustomNetworkCallBack(systemInfoService)
    }


}