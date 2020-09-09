package com.android.walletforest.add_transaction_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Repository
import kotlinx.coroutines.launch

class AddTransactionFragViewModel(private val repository: Repository): ViewModel()
{
    private val currentId=0L
    var transaction : LiveData<Transaction> = MutableLiveData()
    val categories = repository.categoryMap
    val wallets = repository.walletMap

    fun setTransactionId(id:Long)
    {
        if(currentId == id) return
        transaction = repository.getTransaction(id)
    }

    fun updateTransaction(transaction: Transaction)
    {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun insertTransaction(transaction: Transaction)
    {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }
}