package com.android.walletforest.model.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.android.walletforest.model.Entities.Wallet

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet")
    fun getWallets():LiveData<List<Wallet>>


}