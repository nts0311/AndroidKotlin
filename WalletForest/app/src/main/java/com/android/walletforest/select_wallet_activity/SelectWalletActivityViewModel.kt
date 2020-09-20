package com.android.walletforest.select_wallet_activity

import androidx.lifecycle.ViewModel
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository

class SelectWalletActivityViewModel(private val repository: Repository) : ViewModel()
{
    val walletList = repository.walletList

    fun updateWalletMap(wallets: List<Wallet>)
    {
        repository.updateWalletsMap(wallets)
    }
}