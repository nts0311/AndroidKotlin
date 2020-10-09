package com.android.walletforest.transaction_detail_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Repository
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

    fun updateTransaction(transaction: Transaction, oldAmount: Long) {
        if (transaction == this.transaction.value)
            return

        GlobalScope.launch {
            val currentWallet = wallets[transaction.walletId]?.copy()
            repository.updateTransaction(transaction)

            if (currentWallet != null) {

                if (transaction.type == this@TransactionDetailViewModel.transaction.value?.type) {
                    if (transaction.type == Constants.TYPE_EXPENSE)
                        currentWallet.amount += (oldAmount - transaction.amount)
                    else
                        currentWallet.amount -= (oldAmount - transaction.amount)
                } else {
                    if (transaction.type == Constants.TYPE_EXPENSE)
                        currentWallet.amount -= (oldAmount + transaction.amount)
                    else
                        currentWallet.amount += (oldAmount + transaction.amount)
                }

                repository.updateWallet(currentWallet)


            }
        }
    }

    fun insertTransaction(transaction: Transaction) {
        /*GlobalScope.launch {
            val currentWallet = wallets[transaction.walletId]?.copy()
            repository.insertTransaction(transaction)

            if (currentWallet != null) {
                if (transaction.type == Constants.TYPE_EXPENSE)
                    currentWallet.amount -= transaction.amount
                else
                    currentWallet.amount += transaction.amount

                repository.updateWallet(currentWallet)
            }

            val budget = repository.getBudgetByIdSync(transaction.categoryId, currentWallet!!.id)
            if(budget!=null)
            {
                budget.spent += transaction.amount
                repository.updateBudget(budget)
            }

            val category = repository.categoryMap[transaction.categoryId]!!
            if(category.id != category.parentId)
            {
                val parentBudget = repository.getBudgetByIdSync(category.parentId, currentWallet!!.id)
                if(parentBudget!=null)
                {
                    parentBudget.spent += transaction.amount
                    repository.updateBudget(parentBudget)
                }
            }

        }*/

        repository.insertTransaction(transaction)
    }



    fun deleteTransaction(transaction: Transaction) {
        GlobalScope.launch {
            val currentWallet = wallets[transaction.walletId]?.copy()
            repository.deleteTransaction(transaction)

            if (currentWallet != null) {
                if (transaction.type == Constants.TYPE_EXPENSE)
                    currentWallet.amount += transaction.amount
                else
                    currentWallet.amount -= transaction.amount

                repository.updateWallet(currentWallet)
            }
        }
    }
}