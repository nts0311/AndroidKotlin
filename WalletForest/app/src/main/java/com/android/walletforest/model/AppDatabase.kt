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
import kotlinx.coroutines.launch

@Database(entities = [Transaction::class, Category::class, Wallet::class], version = 1)
abstract class AppDatabase() : RoomDatabase() {
    abstract val transactionDao: TransactionDao
    abstract val categoryDao: CategoryDao
    abstract val walletDao: WalletDao

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
                    //TEST DATA
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        val scope= CoroutineScope(Dispatchers.IO)

                        val database=AppDatabase.getInstance(context)

                        scope.launch {
                            database.walletDao.insertWallet(
                                Wallet(1,"Cash", R.drawable.icon, 100000))

                            database.categoryDao.insertCategory(
                                Category(1,1,"Family", Constants.TYPE_EXPENSE,R.drawable.ic_category_family))
                            database.categoryDao.insertCategory(
                                Category(2,1,"Food", Constants.TYPE_EXPENSE,R.drawable.ic_category_foodndrink))
                            database.categoryDao.insertCategory(
                                Category(3,3,"Friend", Constants.TYPE_EXPENSE,R.drawable.ic_category_friendnlover))
                            database.categoryDao.insertCategory(
                                Category(4,1,"Education", Constants.TYPE_INCOME,R.drawable.ic_category_education))

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (12, 1, 1, Constants.TYPE_EXPENSE, 12, "aaa", 1599004800000))

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (1, 2, 1, Constants.TYPE_EXPENSE, 1, "aaa", 1599004800000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (2, 3, 1, Constants.TYPE_EXPENSE, 2, "aaa", 1599004800000)
                            )

                            //5-9
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (3, 3, 1, Constants.TYPE_EXPENSE, 3, "aaa", 1599264000000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (4, 1, 1, Constants.TYPE_EXPENSE, 4, "aaa", 1599264000000)
                            )


                            //13-9
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (5, 2, 1, Constants.TYPE_EXPENSE, 5, "aaa", 1599955200000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (6, 3, 1, Constants.TYPE_EXPENSE, 6, "aaa", 1599955200000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (7, 1, 1, Constants.TYPE_EXPENSE, 7, "aaa", 1599955200000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (8, 4, 1, Constants.TYPE_INCOME, 8, "aaa", 1599955200000)
                            )


                            //24-9
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (9, 4, 1, Constants.TYPE_INCOME, 9, "aaa", 1600905600000)
                            )

                            //2-9
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (10, 1, 1, Constants.TYPE_EXPENSE, 10, "aaa", 1599004800000)
                            )

                            //3-8
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (11, 1, 1, Constants.TYPE_EXPENSE, 11, "aaa", 1596412800000)
                            )
                            //31-8
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (13, 1, 1, Constants.TYPE_EXPENSE, 13, "aaa", 1598832000000)
                            )

                        }
                        //Thread.sleep(5000)
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
    }
}