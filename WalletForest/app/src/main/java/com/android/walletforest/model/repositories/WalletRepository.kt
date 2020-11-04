package com.android.walletforest.model.repositories

import com.android.walletforest.model.Dao.WalletDao
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WalletRepository(private val walletDao: WalletDao) {
    private var _walletsMap: MutableMap<Long, Wallet> = mutableMapOf()
    var walletMap: Map<Long, Wallet> = _walletsMap

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val walletList = walletDao.getWallets().first()

            for (wallet in walletList) {
                _walletsMap[wallet.id] = wallet
            }
        }
    }

    suspend fun insertWallet(wallet: Wallet) {
        walletDao.insertWallet(wallet)

        _walletsMap[wallet.id] = wallet

        //update the master wallet
        val masterWallet = walletMap[1L]
        if (masterWallet != null) {
            masterWallet.amount += wallet.amount
            updateWallet(masterWallet)
        }
    }

    suspend fun updateWallet(wallet: Wallet) {
        walletDao.updateWallet(wallet)

        _walletsMap[wallet.id] = wallet

        //update the master wallet
        val masterWallet = walletMap[1L]
        if (masterWallet != null) {
            val oldAmount = walletMap[wallet.id]?.amount
            masterWallet.amount += (wallet.amount - oldAmount!!)
            walletDao.updateWallet(masterWallet)
        }
    }

    suspend fun deleteWallet(wallet: Wallet) {
        val balance = walletMap[wallet.id]?.amount
        walletDao.deleteWallet(wallet)

        _walletsMap.remove(wallet.id)

        //update the master wallet
        val masterWallet = walletMap[1L]
        if (masterWallet != null) {

            masterWallet.amount -= balance!!
            walletDao.updateWallet(masterWallet)
        }

    }

    fun getWalletById(id: Long) = walletDao.getWalletById(id)
}