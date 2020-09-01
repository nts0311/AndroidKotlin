package com.android.walletforest.model.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.walletforest.model.Entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE time >= :start AND time <= :end")
    fun getTransactionsBetweenRange(start: Long, end: Long): LiveData<List<Transaction>>

    @Query("SELECT time FROM transactions WHERE walletId=:walletId ORDER BY time ASC LIMIT 1")
    fun getOldestTransactionTime(walletId: Long): Flow<Long>

    @Query("SELECT time FROM transactions WHERE walletId=:walletId ORDER BY time DESC LIMIT 1")
    fun getNewestTransactionTime(walletId: Long): Flow<Long>

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}














