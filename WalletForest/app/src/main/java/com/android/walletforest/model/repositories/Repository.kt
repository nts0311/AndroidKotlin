package com.android.walletforest.model.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.android.walletforest.main_activity.TabInfo
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.AppDatabase
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.report_record_fragment.ChartEntryGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class Repository private constructor(val appContext: Context) {
    private val appDatabase = AppDatabase.getInstance(appContext)

    var viewMode = MutableLiveData(ViewType.TRANSACTION)

    private var _categoriesMap: MutableMap<Long, Category> = mutableMapOf()
    var categoryMap: Map<Long, Category> = _categoriesMap

    private var _tabInfoList = MutableLiveData<List<TabInfo>>()
    var tabInfoList: LiveData<List<TabInfo>> = _tabInfoList

    private val walletRepository = WalletRepository(appDatabase.walletDao)
    val walletMap: Map<Long, Wallet>
        get() = walletRepository.walletMap

    private val budgetRepository = BudgetRepository(appDatabase.budgetDao, appDatabase.transactionDao)
    private val transactionRepository = TransactionRepository(
        appDatabase.transactionDao,
        walletRepository,
        appDatabase.budgetDao,
        walletMap,
        _categoriesMap
    )


    //current wallet
    fun setCurrentWallet(walletId: Long) {
        _currentWalletId.value = walletId
    }

    private var _currentWalletId: MutableLiveData<Long> = MutableLiveData()
    var currentWallet: LiveData<Wallet> = Transformations.switchMap(_currentWalletId)
    {
        walletRepository.getWalletById(it).asLiveData()
    }


    var walletList = appDatabase.walletDao.getWallets()

    var currentPage = 0





    /**
   * /\_____/\
    /  o   o  \
   ( ==  ^  == )
    )         (
   (           )
  ( (  )   (  ) )
 (__(__)___(__)__)
     * **/

    fun getBarData(start: Long, end: Long, walletId: Long, timeRange: TimeRange) =
        getTransactionsBetweenRange(start, end, walletId)
            .map { ChartEntryGenerator.getBarChartData(it, start, end, timeRange) }
            .flowOn(Dispatchers.Default)

    fun getPieEntries(
        start: Long,
        end: Long,
        walletId: Long,
        excludeSubCate: Boolean
    ) =
        getTransactionsBetweenRange(start, end, walletId)
            .map { ChartEntryGenerator.getPieEntries(it, excludeSubCate, appContext) }
            .flowOn(Dispatchers.Default)


    fun setTabInfoList(list: List<TabInfo>) {
        _tabInfoList.value = list
    }



    //timeRange: current timeRange
    private var _timeRange = MutableLiveData<TimeRange>()
    var timeRange: LiveData<TimeRange> = _timeRange

    fun setTimeRange(timeRange: TimeRange) {
        _timeRange.value = timeRange
    }

    suspend fun insertWallet(wallet: Wallet) {
        walletRepository.insertWallet(wallet)
    }

    suspend fun updateWallet(wallet: Wallet) {
        walletRepository.updateWallet(wallet)
    }

    suspend fun deleteWallet(wallet: Wallet) {
        walletRepository.deleteWallet(wallet)
        _currentWalletId.postValue(1L)
    }

    fun getWalletById(id: Long) = appDatabase.walletDao.getWalletById(id)

    fun getTransaction(id: Long) = appDatabase.transactionDao.getTransaction(id)

    fun updateTransaction(newTransaction: Transaction, oldTransaction: Transaction) {
        transactionRepository.updateTransaction(newTransaction, oldTransaction)
    }

    fun insertTransaction(transaction: Transaction) {
        transactionRepository.insertTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) {
        transactionRepository.deleteTransaction(transaction)
    }

    //Get the list of transactions in a specific wallet and specific period
    //If the list is not cached, fetch the list from database and cache it
    fun getTransactionsBetweenRange(
        start: Long,
        end: Long,
        walletId: Long
    ): Flow<List<Transaction>> =
        transactionRepository.getTransactionsBetweenRange(start, end, walletId)

    fun getCategoriesLiveData(): LiveData<List<Category>> = appDatabase.categoryDao.getCategories()

    fun updateCategoriesMap(categories: List<Category>) {
        for (category in categories) {
            if (!_categoriesMap.containsKey(category.id))
                _categoriesMap[category.id] = category
        }
    }

    fun getCategoriesByType(type: String) = appDatabase.categoryDao.getCategoriesByType(type)

    fun getAllBudget(walletId: Long): Flow<List<Budget>> = budgetRepository.getAllBudgets(walletId)

    suspend fun insertBudget(newBudget: Budget) {
        budgetRepository.insertBudget(newBudget)
    }

    fun getBudgetById(id: Long):Flow<Budget> = budgetRepository.getBudgetById(id)

    suspend fun deleteBudget(budget: Budget)
    {
        budgetRepository.deleteBudget(budget)
    }

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