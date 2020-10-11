package com.android.walletforest.budget_activity

import androidx.lifecycle.ViewModel
import com.android.walletforest.model.repositories.Repository

class BudgetActivityViewModel(repository: Repository) : ViewModel() {
    val currentWallet = repository.currentWallet
}