package com.android.walletforest.model.repositories

import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Dao.TransactionDao
import com.android.walletforest.model.Dao.WalletDao
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WalletRepository(
    private val walletDao: WalletDao
) {
    private var _walletsMap: MutableMap<Long, Wallet> = mutableMapOf()
    var walletMap: Map<Long, Wallet> = _walletsMap
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            val walletList = walletDao.getWallets().first()

            for (wallet in walletList) {
                _walletsMap[wallet.id] = wallet
            }
        }
    }

    suspend fun insertWallet(wallet: Wallet) {
        val newId = walletDao.insertWallet(wallet)
        wallet.id = newId
        _walletsMap[newId] = wallet
        //update the master wallet
        val masterWallet = _walletsMap[1L]
        if (masterWallet!=null) {
            masterWallet.amount += wallet.amount
            updateWallet(masterWallet)

            _walletsMap[1L] = masterWallet
        }
    }

    suspend fun updateWallet(wallet: Wallet) {
        walletDao.updateWallet(wallet)


        //update the master wallet
        val masterWallet = _walletsMap[1L]
        if (masterWallet!=null) {
            val oldAmount = walletMap[wallet.id]?.amount
            masterWallet.amount += (wallet.amount - oldAmount!!)

            walletDao.updateWallet(masterWallet)

            _walletsMap[1L] = masterWallet

        }

        _walletsMap[wallet.id] = wallet
    }

    suspend fun deleteWallet(wallet: Wallet) {
        val balance = walletMap[wallet.id]?.amount
        walletDao.deleteWallet(wallet)

        //update the master wallet
        val masterWallet = _walletsMap[1L]
        if (masterWallet!=null) {

            masterWallet.amount -= balance!!
            walletDao.updateWallet(masterWallet)

            _walletsMap[1L] = masterWallet
        }

        _walletsMap.remove(wallet.id)
    }

    fun getWalletById(id: Long) = walletDao.getWalletById(id)
}