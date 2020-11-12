package com.android.walletforest.model.repositories

import com.android.walletforest.model.Dao.BudgetDao
import com.android.walletforest.model.Dao.TransactionDao
import com.android.walletforest.model.Entities.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class BudgetRepository(private val budgetDao: BudgetDao, private val transactionDao: TransactionDao) {
    private val budgetsMap = mutableMapOf<Long, Flow<Budget>>()
    private var budgetList: MutableMap<Long, Flow<List<Budget>>> = mutableMapOf()

    fun getAllBudgets(walletId: Long): Flow<List<Budget>> =
        if (budgetList.containsKey(walletId))
            budgetList[walletId]!!
        else {
            val newBudgetList = budgetDao.getBudgetList(walletId).distinctUntilChanged()
            budgetList[walletId] = newBudgetList
            newBudgetList
        }


    fun getBudgetById(id: Long): Flow<Budget> {
        if (!budgetsMap.containsKey(id))
            budgetsMap[id] = budgetDao.getBudgetById(id).distinctUntilChanged()

        return budgetsMap[id]!!
    }

    suspend fun getBudgetByIdSync(cateId: Long, walletId: Long, transactionDate: Long): Budget? =
        budgetDao.getBudgetByCategorySync(cateId, walletId, transactionDate)

    suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget)
    }

    suspend fun deleteBudget(budget: Budget)
    {
        budgetDao.deleteBudget(budget)
    }

    suspend fun insertBudget(budget: Budget)
    {
        budgetDao.insertBudget(budget)
    }
}