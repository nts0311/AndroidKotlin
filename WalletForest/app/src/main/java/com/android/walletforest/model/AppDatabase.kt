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
                                Wallet(1,"All", R.drawable.ic_category_all, 100000,"VND"))
                            database.walletDao.insertWallet(
                                Wallet(2,"Cash", R.drawable.icon, 100000,"VND"))
                            database.walletDao.insertWallet(
                                Wallet(3,"Cash2", R.drawable.icon, 1,"VND"))


                            database.categoryDao.insertCategory(
                                Category(1,1,context.getString(R.string.food_n_beverage), Constants.TYPE_EXPENSE,R.drawable.ic_category_foodndrink))
                            database.categoryDao.insertCategory(
                                Category(2,1,context.getString(R.string.restaurants), Constants.TYPE_EXPENSE,R.drawable.icon_133))
                            database.categoryDao.insertCategory(
                                Category(3,1,context.getString(R.string.cafe), Constants.TYPE_EXPENSE,R.drawable.icon_15))

                            database.categoryDao.insertCategory(
                                Category(4,4,context.getString(R.string.bill_utilities), Constants.TYPE_EXPENSE,R.drawable.icon_135))
                            database.categoryDao.insertCategory(
                                Category(5,4,context.getString(R.string.phone), Constants.TYPE_EXPENSE,R.drawable.icon_134))
                            database.categoryDao.insertCategory(
                                Category(6,4,context.getString(R.string.water), Constants.TYPE_EXPENSE,R.drawable.icon_124))
                            database.categoryDao.insertCategory(
                                Category(7,4,context.getString(R.string.electricity), Constants.TYPE_EXPENSE,R.drawable.icon_125))
                            database.categoryDao.insertCategory(
                                Category(8,4,context.getString(R.string.gas), Constants.TYPE_EXPENSE,R.drawable.icon_139))
                            database.categoryDao.insertCategory(
                                Category(9,4,context.getString(R.string.television), Constants.TYPE_EXPENSE,R.drawable.icon_84))
                            database.categoryDao.insertCategory(
                                Category(10,4,context.getString(R.string.internet), Constants.TYPE_EXPENSE,R.drawable.icon_126))
                            database.categoryDao.insertCategory(
                                Category(11,4,context.getString(R.string.rentals), Constants.TYPE_EXPENSE,R.drawable.icon_136))

                            database.categoryDao.insertCategory(
                                Category(12,12,context.getString(R.string.transportation), Constants.TYPE_EXPENSE,R.drawable.ic_category_transport))
                            database.categoryDao.insertCategory(
                                Category(13,12,context.getString(R.string.taxi), Constants.TYPE_EXPENSE,R.drawable.icon_127))
                            database.categoryDao.insertCategory(
                                Category(14,12,context.getString(R.string.parking_fees), Constants.TYPE_EXPENSE,R.drawable.icon_128))
                            database.categoryDao.insertCategory(
                                Category(15,12,context.getString(R.string.petrol), Constants.TYPE_EXPENSE,R.drawable.icon_129))
                            database.categoryDao.insertCategory(
                                Category(16,12,context.getString(R.string.maintenance), Constants.TYPE_EXPENSE,R.drawable.icon_130))

                            database.categoryDao.insertCategory(
                                Category(17,17,context.getString(R.string.shopping), Constants.TYPE_EXPENSE,R.drawable.ic_category_shopping))
                            database.categoryDao.insertCategory(
                                Category(18,17,context.getString(R.string.clothing), Constants.TYPE_EXPENSE,R.drawable.icon_17))
                            database.categoryDao.insertCategory(
                                Category(19,17,context.getString(R.string.footwear), Constants.TYPE_EXPENSE,R.drawable.icon_131))
                            database.categoryDao.insertCategory(
                                Category(20,17,context.getString(R.string.accessories), Constants.TYPE_EXPENSE,R.drawable.icon_63))
                            database.categoryDao.insertCategory(
                                Category(21,17,context.getString(R.string.electronics), Constants.TYPE_EXPENSE,R.drawable.icon_9))

                            database.categoryDao.insertCategory(
                                Category(22,22,context.getString(R.string.friends_n_lover), Constants.TYPE_EXPENSE,R.drawable.ic_category_friendnlover))

                            database.categoryDao.insertCategory(
                                Category(23,23,context.getString(R.string.entertainment), Constants.TYPE_EXPENSE,R.drawable.ic_category_entertainment))
                            database.categoryDao.insertCategory(
                                Category(24,23,context.getString(R.string.movies), Constants.TYPE_EXPENSE,R.drawable.icon_6))
                            database.categoryDao.insertCategory(
                                Category(25,23,context.getString(R.string.games), Constants.TYPE_EXPENSE,R.drawable.icon_33))

                            database.categoryDao.insertCategory(
                                Category(26,26,context.getString(R.string.travel), Constants.TYPE_EXPENSE,R.drawable.ic_category_travel))

                            database.categoryDao.insertCategory(
                                Category(27,27,context.getString(R.string.health_fitness), Constants.TYPE_EXPENSE,R.drawable.ic_category_medical))
                            database.categoryDao.insertCategory(
                                Category(28,27,context.getString(R.string.sports), Constants.TYPE_EXPENSE,R.drawable.icon_70))
                            database.categoryDao.insertCategory(
                                Category(29,27,context.getString(R.string.doctor), Constants.TYPE_EXPENSE,R.drawable.ic_category_doctor))
                            database.categoryDao.insertCategory(
                                Category(30,27,context.getString(R.string.pharmacy), Constants.TYPE_EXPENSE,R.drawable.ic_category_pharmacy))
                            database.categoryDao.insertCategory(
                                Category(31,27,context.getString(R.string.personal_care), Constants.TYPE_EXPENSE,R.drawable.icon_132))

                            database.categoryDao.insertCategory(
                                Category(32,32,context.getString(R.string.gift_donations), Constants.TYPE_EXPENSE,R.drawable.ic_category_donations))
                            database.categoryDao.insertCategory(
                                Category(33,32,context.getString(R.string.marriage), Constants.TYPE_EXPENSE,R.drawable.icon_10))
                            database.categoryDao.insertCategory(
                                Category(34,32,context.getString(R.string.funeral), Constants.TYPE_EXPENSE,R.drawable.icon_11))
                            database.categoryDao.insertCategory(
                                Category(35,32,context.getString(R.string.charity), Constants.TYPE_EXPENSE,R.drawable.ic_category_give))

                            database.categoryDao.insertCategory(
                                Category(36,36,context.getString(R.string.family), Constants.TYPE_EXPENSE,R.drawable.ic_category_family))
                            database.categoryDao.insertCategory(
                                Category(37,36,context.getString(R.string.children_baby), Constants.TYPE_EXPENSE,R.drawable.icon_38))
                            database.categoryDao.insertCategory(
                                Category(38,36,context.getString(R.string.home_improvement), Constants.TYPE_EXPENSE,R.drawable.icon_8))
                            database.categoryDao.insertCategory(
                                Category(39,36,context.getString(R.string.home_services), Constants.TYPE_EXPENSE,R.drawable.icon_54))
                            database.categoryDao.insertCategory(
                                Category(40,36,context.getString(R.string.pets), Constants.TYPE_EXPENSE,R.drawable.icon_53))

                            database.categoryDao.insertCategory(
                                Category(41,41,context.getString(R.string.education), Constants.TYPE_EXPENSE,R.drawable.ic_category_education))
                            database.categoryDao.insertCategory(
                                Category(42,41,context.getString(R.string.books), Constants.TYPE_EXPENSE,R.drawable.icon_35))

                            database.categoryDao.insertCategory(
                                Category(43,43,context.getString(R.string.investment), Constants.TYPE_EXPENSE,R.drawable.ic_category_invest))

                            database.categoryDao.insertCategory(
                                Category(44,44,context.getString(R.string.business), Constants.TYPE_EXPENSE,R.drawable.icon_59))

                            database.categoryDao.insertCategory(
                                Category(45,45,context.getString(R.string.insurances), Constants.TYPE_EXPENSE,R.drawable.icon_137))

                            database.categoryDao.insertCategory(
                                Category(46,46,context.getString(R.string.fees_n_charges), Constants.TYPE_EXPENSE,R.drawable.icon_138))

                            database.categoryDao.insertCategory(
                                Category(47,47,context.getString(R.string.withdrawal), Constants.TYPE_EXPENSE,R.drawable.icon_withdrawal))

                            database.categoryDao.insertCategory(
                                Category(48,48,context.getString(R.string.other), Constants.TYPE_EXPENSE,R.drawable.icon_22))


                            //income categories
                            database.categoryDao.insertCategory(
                                Category(49,49,context.getString(R.string.award), Constants.TYPE_INCOME,R.drawable.icon_111))
                            database.categoryDao.insertCategory(
                                Category(50,50,context.getString(R.string.interest_money), Constants.TYPE_INCOME,R.drawable.ic_category_interestmoney))
                            database.categoryDao.insertCategory(
                                Category(51,51,context.getString(R.string.salary), Constants.TYPE_INCOME,R.drawable.ic_category_salary))
                            database.categoryDao.insertCategory(
                                Category(52,52,context.getString(R.string.gifts), Constants.TYPE_INCOME,R.drawable.ic_category_give))
                            database.categoryDao.insertCategory(
                                Category(53,53,context.getString(R.string.selling), Constants.TYPE_INCOME,R.drawable.ic_category_selling))
                            database.categoryDao.insertCategory(
                                Category(54,54,context.getString(R.string.parent), Constants.TYPE_INCOME,R.drawable.icon_29))
                            database.categoryDao.insertCategory(
                                Category(55,55,context.getString(R.string.other), Constants.TYPE_INCOME,R.drawable.icon_28))

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (12, 1, 2, Constants.TYPE_EXPENSE, 12, "aaa", 1599004800000))

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (1, 2, 2, Constants.TYPE_EXPENSE, 1, "aaa", 1599004800000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (2, 3, 2, Constants.TYPE_EXPENSE, 2, "aaa", 1599004800000)
                            )

                            //5-9
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (3, 3, 2, Constants.TYPE_EXPENSE, 3, "aaa", 1599264000000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (4, 1, 2, Constants.TYPE_EXPENSE, 4, "aaa", 1599264000000)
                            )


                            //13-9
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (5, 2, 2, Constants.TYPE_EXPENSE, 5, "aaa", 1599955200000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (6, 3, 2, Constants.TYPE_EXPENSE, 6, "aaa", 1599955200000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (7, 1, 2, Constants.TYPE_EXPENSE, 7, "aaa", 1599955200000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (8, 4, 2, Constants.TYPE_INCOME, 8, "aaa", 1599955200000)
                            )


                            //24-9
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (9, 4, 2, Constants.TYPE_INCOME, 9, "aaa", 1600905600000)
                            )

                            //2-9
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (10, 1, 2, Constants.TYPE_EXPENSE, 10, "aaa", 1599004800000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (10, 43, 2, Constants.TYPE_EXPENSE, 20, "aaa", 1599004800000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (11, 21, 2, Constants.TYPE_EXPENSE, 17, "aaa", 1599004800000)
                            )

                            database.transactionDao.insertTransaction(
                                Transaction
                                    (12, 7, 2, Constants.TYPE_EXPENSE, 6, "aaa", 1599004800000)
                            )

                            //3-8
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (13, 1, 2, Constants.TYPE_EXPENSE, 11, "aaa", 1596412800000)
                            )
                            //31-8
                            database.transactionDao.insertTransaction(
                                Transaction
                                    (14, 1, 2, Constants.TYPE_EXPENSE, 13, "aaa", 1598832000000)
                            )

                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
    }
}