package com.android.walletforest.model.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet")
    fun getWallets():LiveData<List<Wallet>>

    @Query("SELECT * FROM wallet LIMIT 1")
    fun getWallet(): LiveData<Wallet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet)

    @Update
    suspend fun updateWallet(wallet: Wallet)

    @Delete
    suspend fun deleteWallet(wallet: Wallet)
}