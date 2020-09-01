package com.android.walletforest.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.walletforest.R
import com.android.walletforest.model.Entities.Wallet

class Repository private constructor(appContext: Context) {
    val appDatabase = AppDatabase.getInstance(appContext)

    //FOR TESTING
    fun getOldestTransactionDate():LiveData<Long> = MutableLiveData<Long>(1582995600000)
    fun getNewestTransactionDate():LiveData<Long> = MutableLiveData<Long>(1590858000000)

    fun getFirstWallet():LiveData<Wallet> =
        MutableLiveData(Wallet(8,"Cash", R.drawable.ic_bk_cashbook, 10000))


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