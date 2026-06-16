package com.example.lokastay.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lokastay.data.entity.FavoriteCollection

@Dao
interface CollectionDao {

    @Query("SELECT * FROM favorite_collections WHERE userId = :userId")
    suspend fun getCollectionsByUserId(userId: Int): List<FavoriteCollection>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: FavoriteCollection)

    @Query("DELETE FROM favorite_collections WHERE id = :collectionId")
    suspend fun deleteCollectionById(collectionId: Int)

    @Query("UPDATE favorite_collections SET name = :newName WHERE id = :collectionId")
    suspend fun updateCollectionName(collectionId: Int, newName: String)
}