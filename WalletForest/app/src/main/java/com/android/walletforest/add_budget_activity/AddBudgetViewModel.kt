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
    lateinit var currentBudget: LiveData<Budget>

    fun insertBudget(newBudget: Budget){
        repository.insertBudget(newBudget)
    }

    fun updateBudget(Budget: Budget) {
        repository.updateBudget(Budget)
    }

    fun setBudgetId(id: Long) {
        currentBudgetId = id
        currentBudget = repository.getBudgetById(id).asLiveData()
    }
}