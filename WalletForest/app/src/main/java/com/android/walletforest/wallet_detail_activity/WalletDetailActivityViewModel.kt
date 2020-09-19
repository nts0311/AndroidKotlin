package com.android.walletforest.wallet_detail_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WalletDetailActivityViewModel(private val repository: Repository) : ViewModel() {

    var wallet: LiveData<Wallet> = MutableLiveData()

    fun setCurrentWallet(id: Long) {
        wallet = repository.getWalletById(id)
    }

    fun addWallet(wallet: Wallet) {
        GlobalScope.launch {
            repository.insertWallet(wallet)
        }
    }

    fun updateWallet(newWallet: Wallet)
    {
        GlobalScope.launch {
            repository.updateWallet(newWallet)
        }
    }

    fun deleteCurrentWallet()
    {
        GlobalScope.launch {
            repository.deleteWallet(wallet.value!!)
        }
    }
}