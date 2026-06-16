package com.example.lokastay.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lokastay.data.entity.Villa

@Dao
interface VillaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(villas: List<Villa>)

    @Query("SELECT * FROM villas")
    suspend fun getAllVillas(): List<Villa>

    @Query("SELECT * FROM villas WHERE id = :villaId")
    suspend fun getVillaById(villaId: Int): Villa

    @Query("""
        SELECT * FROM villas
        WHERE (location LIKE '%' || :location || '%' 
           OR name LIKE '%' || :location || '%' 
           OR description LIKE '%' || :location || '%')
        AND maxGuest >= :guestCount
        AND rating >= :rating
        AND pricePerNight BETWEEN :minPrice AND :maxPrice
        AND hasWifi >= :requireWifi
        AND hasPool >= :requirePool
        AND hasAc >= :requireAc
        AND hasLaundry >= :requireLaundry
        AND hasTv >= :requireTv
        AND hasGym >= :requireGym
        AND hasHeater >= :requireHeater
        AND hasParking >= :requireParking
        AND hasBbq >= :requireBbq
        AND hasBathtub >= :requireBathtub
        AND hasGarden >= :requireGarden
        AND hasKitchen >= :requireKitchen
        AND id NOT IN (
            SELECT villaId FROM transactions 
            WHERE paymentStatus = 'SUCCESS'
            AND checkInDate != '' 
            AND checkOutDate != ''
            AND checkInDate < :checkOutDate 
            AND checkOutDate > :checkInDate
        )
    """)
    suspend fun filterVilla(
        location: String,
        guestCount: Int,
        checkInDate: String,
        checkOutDate: String,
        rating: Float,
        minPrice: Double,
        maxPrice: Double,
        requireWifi: Boolean,
        requirePool: Boolean,
        requireAc: Boolean,
        requireLaundry: Boolean,
        requireTv: Boolean,
        requireGym: Boolean,
        requireHeater: Boolean,
        requireParking: Boolean,
        requireBbq: Boolean,
        requireBathtub: Boolean,
        requireGarden: Boolean,
        requireKitchen: Boolean
    ): List<Villa>

    @Query("SELECT COUNT(*) FROM villas")
    suspend fun countVillas(): Int
}