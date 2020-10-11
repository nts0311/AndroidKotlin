package com.android.walletforest.transaction_detail_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.repositories.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TransactionDetailViewModel(private val repository: Repository) : ViewModel() {
    private val currentId = 0L
    var transaction: LiveData<Transaction> = MutableLiveData()
    val categories = repository.categoryMap
    val wallets = repository.walletMap

    fun setTransactionId(id: Long) {
        if (currentId == id) return
        transaction = repository.getTransaction(id)
    }

    fun updateTransaction(transaction: Transaction) {
        if (transaction == this.transaction.value)
            return

        repository.updateTransaction(transaction, this.transaction.value!!)
    }

    fun insertTransaction(transaction: Transaction) {
        repository.insertTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) {
        repository.deleteTransaction(transaction)
    }
}