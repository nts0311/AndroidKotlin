package com.android.walletforest.model.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.walletforest.model.Entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransaction(id: Long): LiveData<Transaction>

    @Query("SELECT * FROM transactions WHERE (date >= :start AND date <= :end) AND (walletId = :walletId)")
    fun getTransactionsBetweenRangeOfWallet(
        start: Long,
        end: Long,
        walletId: Long
    ): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE (date >= :start AND date <= :end) ORDER BY date")
    fun getTransactionsBetweenRange(start: Long, end: Long) : Flow<List<Transaction>>

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}














