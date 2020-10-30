package com.android.walletforest.budget_detail_activity

import androidx.lifecycle.*
import com.android.walletforest.model.repositories.BudgetRepository
import com.android.walletforest.model.repositories.Repository

class BudgetDetailViewModel(repository: Repository) : ViewModel() {

    var budgetId = 0L

    var currentBudget = liveData {
        emitSource(repository.getBudgetById(budgetId).asLiveData())
    }

    var categoryMap = repository.categoryMap
    var walletMap = repository.walletMap
}