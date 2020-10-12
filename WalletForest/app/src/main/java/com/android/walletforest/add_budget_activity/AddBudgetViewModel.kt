package com.android.walletforest.add_budget_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AddBudgetViewModel(private val repository: Repository) : ViewModel() {
    fun insertBudget(newBudget: Budget) {
        var spent = 0L

        GlobalScope.launch {
            repository.getTransactionsBetweenRange(0L, 0L, 0L)
                .map {
                    it.fold(0L) { sum, transaction -> sum + transaction.amount }
                }
                .flowOn(Dispatchers.Default)
                .collect {
                    spent = it
                    return@collect
                }

            newBudget.spent = spent
            repository.insertBudget(newBudget)
        }
    }

    val currentWallet = repository.currentWallet
    val categoriesMap = repository.categoryMap
}