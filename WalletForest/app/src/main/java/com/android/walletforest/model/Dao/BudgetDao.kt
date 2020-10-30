package com.android.walletforest.model.Dao

import androidx.room.*
import com.android.walletforest.model.Entities.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE walletId=:walletId")
    fun getBudgetList(walletId: Long): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    fun getBudgetById(id: Long): Flow<Budget>

    @Query("SELECT * FROM budgets WHERE categoryId = :cateId AND walletId=:walletId")
    fun getBudgetByCategory(cateId: Long, walletId: Long): Flow<Budget>

    @Query("SELECT * FROM budgets WHERE categoryId = :cateId AND walletId=:walletId ORDER BY endDate DESC LIMIT 1")
    suspend fun getBudgetByCategorySync(cateId: Long, walletId: Long): Budget?

    @Query("SELECT * FROM budgets WHERE walletId=:walletId AND categoryId=-1 ORDER BY endDate DESC LIMIT 1")
    suspend fun getAllCategoriesBudgetSync(walletId: Long): Budget?

    @Insert
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)
}