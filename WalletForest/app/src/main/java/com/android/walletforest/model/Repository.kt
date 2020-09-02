package com.android.walletforest.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet

class Repository private constructor(appContext: Context) {
    val appDatabase = AppDatabase.getInstance(appContext)

    fun getFirstWallet():LiveData<Wallet> =
        appDatabase.walletDao.getWallet()

    fun getTransactionsBetweenRange(start:Long, end:Long) : LiveData<List<Transaction>>
        = appDatabase.transactionDao.getTransactionsBetweenRange(start, end)

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