package com.android.walletforest.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.walletforest.R
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Dao.CategoryDao
import com.android.walletforest.model.Dao.TransactionDao
import com.android.walletforest.model.Dao.WalletDao
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Database(entities = [Transaction::class, Category::class, Wallet::class], version = 1)
abstract class AppDatabase() : RoomDatabase() {
    abstract val TransactionDao: TransactionDao
    abstract val CategoryDao: CategoryDao
    abstract val WalletDao: WalletDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(appContext: Context): AppDatabase =
            synchronized(AppDatabase::class.java)
            {
                return INSTANCE ?: buildDatabase(appContext.applicationContext).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context) =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .addCallback(object : Callback()
                {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        val scope= CoroutineScope(Dispatchers.IO)

                        val database=AppDatabase.getInstance(context)

                        scope.launch {
                            database.WalletDao.insertWallet(
                                Wallet(0,"Cash", R.drawable.ic_bk_cashbook, 100000))

                            database.CategoryDao.insertCategory(
                                Category(0,0,"Food",R.drawable.ic_category_foodndrink)
                            )
                            database.CategoryDao.insertCategory(
                                Category(1,1,"Family",R.drawable.ic_category_family))

                            database.TransactionDao.insertTransaction(
                                Transaction(0,0,0,Constants.TYPE_EXPENSE,1000,"aaa",1582995600000))

                            database.TransactionDao.insertTransaction(
                                Transaction(1,1,0,Constants.TYPE_EXPENSE,2000,"bbb",1584118800000))

                            database.TransactionDao.insertTransaction(
                                Transaction(2,0,0,Constants.TYPE_EXPENSE,3000,"ccc",1585242000000))

                            database.TransactionDao.insertTransaction(
                                Transaction(3,1,0,Constants.TYPE_EXPENSE,4000,"ddd",1585760400000))

                            database.TransactionDao.insertTransaction(
                                Transaction(4,0,0,Constants.TYPE_EXPENSE,5000,"eee",1588352400000))
                        }

                    }
                })
                .fallbackToDestructiveMigration()
                .build()
    }
}