package com.android.walletforest.model.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet")
    fun getWallets(): Flow<List<Wallet>>

    @Query("SELECT * FROM wallet LIMIT 1")
    fun getWallet(): Flow<Wallet>

    @Query("SELECT * FROM wallet WHERE id=:id")
    fun getWalletById(id: Long): Flow<Wallet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet) : Long

    @Update
    suspend fun updateWallet(wallet: Wallet)

    @Delete
    suspend fun deleteWallet(wallet: Wallet)
}