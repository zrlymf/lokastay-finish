package com.example.lokastay.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lokastay.data.entity.Review

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(review: Review)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(reviews: List<Review>)

    @Query("""
        SELECT * FROM reviews
        WHERE villaId = :villaId
    """)
    suspend fun getReviews(villaId: Int): List<Review>

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun countReviews(): Int
}