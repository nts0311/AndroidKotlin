package com.android.walletforest.model

import com.android.walletforest.model.Dao.BudgetDao
import com.android.walletforest.model.Entities.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BudgetRepository(private val budgetDao: BudgetDao) {
    private val budgetsMap = mutableMapOf<Long, Flow<Budget>>()
    private var budgetList: Flow<List<Budget>>? = null

    fun getAllBudgets(): Flow<List<Budget>> {
        if (budgetList == null)
            budgetList = budgetDao.getBudgetList()

        return budgetList!!
    }

    fun getBudgetById(id:Long) : Flow<Budget>
    {
        if(!budgetsMap.containsKey(id))
            budgetsMap[id] = budgetDao.getBudgetById(id)

        return budgetsMap[id]!!
    }


}