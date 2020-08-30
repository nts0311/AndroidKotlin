package com.android.walletforest.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.walletforest.model.Dao.CategoryDao
import com.android.walletforest.model.Dao.TransactionDao
import com.android.walletforest.model.Dao.WalletDao
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet

@Database(entities = [Transaction::class, Category::class, Wallet::class], version = 1)
abstract class AppDatabase() : RoomDatabase() {
    abstract val TransactionDao: TransactionDao
    abstract val CategoryDao: CategoryDao
    abstract val WalletDao: WalletDao

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null

        fun getInstance(appContext: Context): AppDatabase =
            synchronized(AppDatabase::class.java)
            {
                return INSTANCE ?: buildDatabase(appContext.applicationContext).also {
                    INSTANCE = it
                }
            }

        fun buildDatabase(context: Context) =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .fallbackToDestructiveMigration()
                .build()
    }
}