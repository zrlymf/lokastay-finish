package com.example.lokastay.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lokastay.data.dao.CollectionDao
import com.example.lokastay.data.dao.FavoriteDao
import com.example.lokastay.data.dao.ReviewDao
import com.example.lokastay.data.dao.TransactionDao
import com.example.lokastay.data.dao.UserDao
import com.example.lokastay.data.dao.VillaDao
import com.example.lokastay.data.entity.Favorite
import com.example.lokastay.data.entity.FavoriteCollection
import com.example.lokastay.data.entity.Review
import com.example.lokastay.data.entity.Transaction
import com.example.lokastay.data.entity.User
import com.example.lokastay.data.entity.Villa

@TypeConverters(Converters::class)
@Database(
    entities = [
        User::class,
        Villa::class,
        Review::class,
        Transaction::class,
        Favorite::class,
        FavoriteCollection::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun villaDao(): VillaDao
    abstract fun reviewDao(): ReviewDao
    abstract fun transactionDao(): TransactionDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun collectionDao(): CollectionDao
}