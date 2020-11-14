package com.android.walletforest.model.repositories

import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Dao.BudgetDao
import com.android.walletforest.model.Dao.TransactionDao
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.Entities.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao,
    private val categoriesMap: Map<Long, Category>
) {
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

    fun insertBudget(newBudget: Budget): Job {
        return GlobalScope.launch {



            newBudget.spent = transactionDao.getTransactionsBetweenRangeOfWallet(
                newBudget.startDate,
                newBudget.endDate,
                newBudget.walletId
            )
                .map {

                    var result = it.filter { transaction -> transaction.type == Constants.TYPE_EXPENSE }

                    result = if (newBudget.categoryId != -1L)
                        it.filter { transaction -> transaction.categoryId == newBudget.categoryId
                                    || categoriesMap[transaction.categoryId]!!.parentId == newBudget.categoryId
                        }
                    else result

                    result
                }
                .map {
                    it.fold(0L) { sum, transaction -> sum + transaction.amount }
                }
                .flowOn(Dispatchers.Default)
                .first()

            budgetDao.insertBudget(newBudget)
        }
    }

    fun updateBudget(budget: Budget) {
        GlobalScope.launch {
            deleteBudget(budget).join()
            insertBudget(budget).join()
        }
    }

    fun deleteBudget(budget: Budget): Job {
        return GlobalScope.launch {
            budgetDao.deleteBudget(budget)
        }
    }
}