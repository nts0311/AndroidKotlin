package com.android.walletforest.add_budget_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddBudgetViewModel(private val repository: Repository) : ViewModel() {

    val currentWallet = repository.currentWallet
    val categoriesMap = repository.categoryMap
    val walletMap = repository.walletMap

    private var currentBudgetId = -1L
    lateinit var currentBudget : LiveData<Budget>

    fun insertBudget(newBudget: Budget) : Job {
        return GlobalScope.launch {
            newBudget.spent = repository.getTransactionsBetweenRange(
                newBudget.startDate,
                newBudget.endDate,
                currentWallet.value!!.id
            )
                .map {
                    it.filter { transaction ->
                        transaction.categoryId == newBudget.categoryId
                                || categoriesMap[transaction.categoryId]!!.parentId == newBudget.categoryId
                    }
                }
                .map {
                    it.fold(0L) { sum, transaction -> sum + transaction.amount }
                }
                .flowOn(Dispatchers.Default)
                .first()

            repository.insertBudget(newBudget)
        }
    }

    fun updateBudget(budget: Budget)
    {
        GlobalScope.launch {
            deleteBudget(budget).join()
            insertBudget(budget).join()
        }
    }

    private fun deleteBudget(budget: Budget) : Job
    {
        return GlobalScope.launch {
            repository.deleteBudget(budget)
        }
    }

    fun setBudgetId(id:Long)
    {
        currentBudgetId = id
        currentBudget = repository.getBudgetById(id).asLiveData()
    }
}