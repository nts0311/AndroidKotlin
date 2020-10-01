package com.android.walletforest.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.android.walletforest.main_activity.TabInfo
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class Repository private constructor(val appContext: Context) {
    private val appDatabase = AppDatabase.getInstance(appContext)

    var viewMode = MutableLiveData(ViewType.TRANSACTION)

    //caching list of transactions of each wallet, avoiding database query
    private var fetchedRange: MutableMap<String, Flow<List<Transaction>>> = mutableMapOf()

    private var _categoriesMap: MutableMap<Long, Category> = mutableMapOf()
    var categoryMap: Map<Long, Category> = _categoriesMap

    private var _walletsMap: MutableMap<Long, Wallet> = mutableMapOf()
    var walletMap: Map<Long, Wallet> = _walletsMap

    //tabInfoList: List of Tab with period for each fragment in viewpager to correctly fetch transactions
    private var _tabInfoList = MutableLiveData<List<TabInfo>>()
    var tabInfoList: LiveData<List<TabInfo>> = _tabInfoList

    //current wallet
    private var _currentWalletId: MutableLiveData<Long> = MutableLiveData()
    var currentWallet: LiveData<Wallet> = Transformations.switchMap(_currentWalletId)
    {
        appDatabase.walletDao.getWalletById(it)
    }

    var walletList = appDatabase.walletDao.getWallets()

    var currentPage = 0

    fun getBarData(start: Long, end: Long, walletId: Long, timeRange: TimeRange) =
        getTransactionsBetweenRange(start, end, walletId)
            .distinctUntilChanged()
            .map { ChartEntryGenerator.getBarEntries(it, start, end, timeRange) }
            .flowOn(Dispatchers.Default)


    fun setTabInfoList(list: List<TabInfo>) {
        _tabInfoList.value = list
    }

    fun setCurrentWallet(walletId: Long) {
        _currentWalletId.value = walletId
    }

    //timeRange: current timeRange
    private var _timeRange = MutableLiveData<TimeRange>()
    var timeRange: LiveData<TimeRange> = _timeRange

    fun setTimeRange(timeRange: TimeRange) {
        _timeRange.value = timeRange
    }

    suspend fun insertWallet(wallet: Wallet) {
        appDatabase.walletDao.insertWallet(wallet)

        //update the master wallet
        val masterWallet = walletMap[1L]
        if (masterWallet != null) {
            masterWallet.amount += wallet.amount
            updateWallet(masterWallet)
        }
    }

    suspend fun updateWallet(wallet: Wallet) {
        //update the master wallet
        val masterWallet = walletMap[1L]
        if (masterWallet != null) {
            val oldAmount = walletMap[wallet.id]?.amount
            masterWallet.amount += (wallet.amount - oldAmount!!)
            appDatabase.walletDao.updateWallet(masterWallet)
        }

        appDatabase.walletDao.updateWallet(wallet)
    }

    suspend fun deleteWallet(wallet: Wallet) {
        val balance = walletMap[wallet.id]?.amount
        appDatabase.walletDao.deleteWallet(wallet)

        //update the master wallet
        val masterWallet = walletMap[1L]
        if (masterWallet != null) {

            masterWallet.amount -= balance!!
            appDatabase.walletDao.updateWallet(masterWallet)
        }
        _currentWalletId.postValue(1L)
    }

    fun getWalletById(id: Long) = appDatabase.walletDao.getWalletById(id)

    fun updateWalletsMap(wallets: List<Wallet>) {
        _walletsMap.clear()
        for (wallet in wallets) {
            _walletsMap[wallet.id] = wallet
        }
    }

    fun getTransaction(id: Long) = appDatabase.transactionDao.getTransaction(id)

    suspend fun updateTransaction(transaction: Transaction) {
        appDatabase.transactionDao.updateTransaction(transaction)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        appDatabase.transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        appDatabase.transactionDao.deleteTransaction(transaction)
    }

    //Get the list of transactions in a specific wallet and specific period
    //If the list is not cached, fetch the list from database and cache it
    fun getTransactionsBetweenRange(
        start: Long,
        end: Long,
        walletId: Long
    ): Flow<List<Transaction>> {

        val key: String = if (walletId == 1L)
            "all-$start-$end"
        else
            "$walletId-$start-$end"

        return if (fetchedRange.containsKey(key))
            fetchedRange[key]!!
        else {

            val transactions =
                if (walletId == 1L)
                    appDatabase.transactionDao.getTransactionsBetweenRange(start, end)
                else
                    appDatabase.transactionDao.getTransactionsBetweenRangeOfWallet(
                        start,
                        end,
                        walletId
                    )

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