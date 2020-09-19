package com.android.walletforest.wallet_detail_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WalletDetailActivityViewModel(private val repository: Repository) : ViewModel() {

    var wallet : LiveData<Wallet> = MutableLiveData()

    fun setCurrentWallet(id:Long)
    {
        wallet
    }

    fun addWallet(wallet : Wallet)
    {
        GlobalScope.launch {
            repository.insertWallet(wallet)
        }
    }
}