package com.android.walletforest.select_wallet_activity

import androidx.lifecycle.ViewModel
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.repositories.Repository

class SelectWalletActivityViewModel(private val repository: Repository) : ViewModel()
{
    val walletList = repository.walletList

    fun updateWalletMap(wallets: List<Wallet>)
    {
        repository.updateWalletsMap(wallets)
    }

    fun selectWallet(walletId: Long) {
        repository.setCurrentWallet(walletId)
    }
}