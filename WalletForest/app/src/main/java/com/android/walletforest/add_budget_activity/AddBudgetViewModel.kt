package com.android.walletforest.add_budget_activity

import androidx.lifecycle.ViewModel
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.repositories.Repository

class AddBudgetViewModel(repository: Repository) : ViewModel() {
    fun insertBudget(newBudget: Budget) {

    }

    val currentWallet = repository.currentWallet
    val categoriesMap = repository.categoryMap
}