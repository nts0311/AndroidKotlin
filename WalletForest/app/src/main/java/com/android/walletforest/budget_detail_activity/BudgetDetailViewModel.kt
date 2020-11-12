package com.android.walletforest.budget_detail_activity

import androidx.lifecycle.*
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.repositories.BudgetRepository
import com.android.walletforest.model.repositories.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BudgetDetailViewModel(private val repository: Repository) : ViewModel() {

    var budgetId = 0L

    var currentBudget = liveData {
        emitSource(repository.getBudgetById(budgetId).asLiveData())
    }

    var categoryMap = repository.categoryMap
    var walletMap = repository.walletMap

    fun deleteBudget(budget: Budget)
    {
        repository.deleteBudget(budget)
    }
}