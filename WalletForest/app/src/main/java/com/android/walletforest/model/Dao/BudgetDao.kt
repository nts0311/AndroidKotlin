package com.android.walletforest.model.Dao

import androidx.room.*
import com.android.walletforest.model.Entities.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets")
    fun getBudgetList() : Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    fun getBudgetById(id : Long) : Flow<Budget>

    @Query("SELECT * FROM budgets WHERE categoryId = :cateId")
    fun getBudgetByCategory(cateId : Long) : Flow<Budget>

    @Query("SELECT * FROM budgets WHERE categoryId = :cateId")
    suspend fun getBudgetByCategorySync(cateId: Long) : Budget

    @Insert
    fun insertBudget(budget: Budget)

    @Update
    fun updateBudget(budget: Budget)

    @Delete
    fun deleteBudget(budget: Budget)
}