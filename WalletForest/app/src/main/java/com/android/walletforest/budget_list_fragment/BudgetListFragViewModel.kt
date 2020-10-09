package com.android.walletforest.budget_list_fragment

import androidx.lifecycle.*
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class BudgetListFragViewModel(private val repository: Repository) : ViewModel() {
    var filterRunningBudgets: Boolean = true
    private val currentWalletId = MutableLiveData<Long>(-1)
    var currentWallet = repository.currentWallet

    var budgetList = currentWalletId.switchMap {newWalletId->
        val now = System.currentTimeMillis()

        repository.getAllBudget(newWalletId).map {budgetList->
            if(filterRunningBudgets)
                budgetList.filter { budget -> budget.endDate >= now}
            else
                budgetList.filter { budget -> budget.endDate < now }
        }
            .flowOn(Dispatchers.Default)
            .asLiveData()
    }

    fun onWalletChanged(newWalletId:Long)
    {
        currentWalletId.value = newWalletId
    }
}