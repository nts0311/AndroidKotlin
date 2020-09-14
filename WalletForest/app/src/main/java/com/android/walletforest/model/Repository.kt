package com.android.walletforest.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.walletforest.TransactionsFragment.TabInfo
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet
import java.sql.Time

class Repository private constructor(appContext: Context) {
    private val appDatabase = AppDatabase.getInstance(appContext)

    var viewMode = MutableLiveData(ViewType.TRANSACTION)

    //caching list of transaction for each fragment, avoiding database query
    private var fetchedRange: MutableMap<String, LiveData<List<Transaction>>> = mutableMapOf()

    private var _categoriesMap: MutableMap<Long, Category> = mutableMapOf()
    var categoryMap: Map<Long, Category> = _categoriesMap

    private var _walletsMap: MutableMap<Long, Wallet> = mutableMapOf()
    var walletMap: Map<Long, Wallet> = _walletsMap

    //tabInfoList: List of Tab with period for each fragment in viewpager to correctly fetch transactions
    private var _tabInfoList = MutableLiveData<List<TabInfo>>()
    var tabInfoList : LiveData<List<TabInfo>> = _tabInfoList

    fun setTabInfoList(list : List<TabInfo>)
    {
        _tabInfoList.value = list
    }

    //timeRange: current timeRange
    private var _timeRange = MutableLiveData<TimeRange>()
    var timeRange : LiveData<TimeRange> = _timeRange

    fun setTimeRange(timeRange: TimeRange)
    {
        _timeRange.value = timeRange
    }

    fun getFirstWallet() = appDatabase.walletDao.getWallet()

    fun getWallets() = appDatabase.walletDao.getWallets()

    fun updateWalletsMap(wallets: List<Wallet>) {
        for (wallet in wallets) {
            if (!_walletsMap.containsKey(wallet.id))
                _walletsMap[wallet.id] = wallet
        }
    }

    fun getTransaction(id: Long) = appDatabase.transactionDao.getTransaction(id)

    suspend fun updateTransaction(transaction: Transaction)
    {
        appDatabase.transactionDao.updateTransaction(transaction)
    }

    suspend fun insertTransaction(transaction: Transaction)
    {
        appDatabase.transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction)
    {
        appDatabase.transactionDao.deleteTransaction(transaction)
    }

    fun getTransactionsBetweenRange(start: Long, end: Long): LiveData<List<Transaction>> {
        val key = "$start-$end"
        return if (fetchedRange.containsKey(key))
            fetchedRange[key]!!
        else {
            val transactions = appDatabase.transactionDao.getTransactionsBetweenRange(start, end)
            fetchedRange[key] = transactions
            transactions
        }
    }

    fun getCategoriesLiveData(): LiveData<List<Category>> = appDatabase.categoryDao.getCategories()

    fun updateCategoriesMap(categories: List<Category>) {
        for (category in categories) {
            if (!_categoriesMap.containsKey(category.id))
                _categoriesMap[category.id] = category
        }
    }

    fun getCategoriesByType(type: String) = appDatabase.categoryDao.getCategoriesByType(type)

    companion object {
        private var instance: Repository? = null

        fun getInstance(appContext: Context): Repository {
            synchronized(Repository::class.java)
            {
                return instance ?: Repository(appContext).also {
                    instance = it
                }
            }
        }
    }
}