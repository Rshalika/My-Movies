package com.strawhat.mymovies.services.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {

    @Query("select * from  MovieEntity m where m.id = :id")
    fun findById(id: Int): MovieEntity?

    @Query("select * from  MovieEntity m where m.is_favorite = 1 LIMIT :pageSize OFFSET :offset ")
    fun getFavorites(pageSize: Int, offset: Int): List<MovieEntity>

    @Query("select count(*) from  MovieEntity m where m.is_favorite = 1")
    fun countFavorites(): Long

    @Update
    fun update(movieEntity: MovieEntity)

    @Insert
    fun insert(movieEntity: MovieEntity)

}