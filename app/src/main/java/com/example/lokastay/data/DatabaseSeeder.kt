package com.example.lokastay.data

import com.example.lokastay.data.database.AppDatabase

object DatabaseSeeder {

    suspend fun seedIfNeeded(db: AppDatabase) {

        if (db.userDao().countUsers() == 0) {
            db.userDao().insertAll(DummyData.users)
        }

        if (db.villaDao().countVillas() == 0) {
            db.villaDao().insertAll(DummyData.villas)
        }

        if (db.reviewDao().countReviews() == 0) {
            db.reviewDao().insertAll(DummyData.reviews)
        }

        if (db.favoriteDao().countFavorites() == 0) {
            db.favoriteDao().insertAll(DummyData.favorites)
        }
    }
}