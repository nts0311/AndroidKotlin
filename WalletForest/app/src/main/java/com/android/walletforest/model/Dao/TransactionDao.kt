package com.android.walletforest.model.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.walletforest.model.Entities.Transaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE time >= :start AND time <= :end")
    fun getTransactionsBetweenRange(start: Long, end: Long): LiveData<List<Transaction>>

    @Query("SELECT time FROM transactions ORDER BY time ASC LIMIT 1")
    suspend fun getOldestTransactionTime(): Long

    @Query("SELECT time FROM transactions ORDER BY time DESC LIMIT 1")
    suspend fun getNewestTransactionTime(): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}














