package com.android.walletforest.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet

class Repository private constructor(appContext: Context) {
    private val appDatabase = AppDatabase.getInstance(appContext)

    var viewMode = MutableLiveData(ViewType.TRANSACTION)

    private var fetchedRange: MutableMap<String, LiveData<List<Transaction>>> = mutableMapOf()

    private var _categoriesMap: MutableMap<Long, Category> = mutableMapOf()
    var categoryMap: Map<Long, Category> = _categoriesMap

    private var _walletsMap: MutableMap<Long, Wallet> = mutableMapOf()
    var walletMap: Map<Long, Wallet> = _walletsMap

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