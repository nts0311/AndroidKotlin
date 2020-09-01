package com.android.walletforest.TransactionsFragment

import androidx.lifecycle.ViewModel
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository

class TransactionsFragViewModel(val repository: Repository):ViewModel()
{
    private lateinit var currentWallet:Wallet

    init {

    }
}