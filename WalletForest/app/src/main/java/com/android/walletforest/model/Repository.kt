package com.android.walletforest.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.flow.Flow

class Repository private constructor(appContext: Context) {
    val appDatabase = AppDatabase.getInstance(appContext)

    fun getOldestTransactionDate(walletId : Long): LiveData<Long>
            = appDatabase.TransactionDao.getOldestTransactionTime(walletId)
    fun getNewestTransactionDate(walletId : Long): LiveData<Long>
            = appDatabase.TransactionDao.getNewestTransactionTime(walletId)

    fun getFirstWallet():LiveData<Wallet> =
        appDatabase.WalletDao.getWallet()


    companion object {
        var instance: Repository? = null

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