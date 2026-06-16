package com.example.lokastay.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lokastay.data.entity.Favorite

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(
        favorite: Favorite
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(
        favorites: List<Favorite>
    )

    @Delete
    suspend fun removeFavorite(
        favorite: Favorite
    )

    @Query("""
        SELECT * FROM favorites
        WHERE userId = :userId
    """)
    suspend fun getFavorites(
        userId: Int
    ): List<Favorite>

    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun countFavorites(): Int

    @Query("DELETE FROM favorites WHERE collectionId = :collectionId")
    suspend fun deleteFavoritesByCollection(collectionId: Int)

    @Query("DELETE FROM favorites WHERE userId = :userId AND villaId = :villaId")
    suspend fun removeAllFavoritesByVilla(userId: Int, villaId: Int)

    @Query("DELETE FROM favorites WHERE userId = :userId AND villaId = :villaId AND collectionId = :collectionId")
    suspend fun removeFavoriteFromCollection(userId: Int, villaId: Int, collectionId: Int)
}