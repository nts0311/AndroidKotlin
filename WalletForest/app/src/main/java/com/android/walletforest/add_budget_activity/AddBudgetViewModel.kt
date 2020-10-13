package com.android.walletforest.add_budget_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddBudgetViewModel(private val repository: Repository) : ViewModel() {

    val currentWallet = repository.currentWallet
    val categoriesMap = repository.categoryMap

    fun insertBudget(newBudget: Budget) {
        GlobalScope.launch {
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


}