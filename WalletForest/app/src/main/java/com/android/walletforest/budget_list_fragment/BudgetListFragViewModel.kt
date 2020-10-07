package com.android.walletforest.budget_list_fragment

import androidx.lifecycle.*
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class BudgetListFragViewModel(private val repository: Repository) : ViewModel() {
    var filterRunningBudgets: Boolean = true

    var budgetList : LiveData<List<Budget>> = liveData {
        val now = System.currentTimeMillis()

        val result = repository.getAllBudget().map {
            if(filterRunningBudgets)
                it.filter { budget -> budget.endDate >= now}
            else
                it.filter { budget -> budget.endDate < now }
        }
            .flowOn(Dispatchers.Default)
            .asLiveData()

        emitSource(result)
    }
}