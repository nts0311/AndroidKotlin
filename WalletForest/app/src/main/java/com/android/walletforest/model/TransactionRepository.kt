package com.android.walletforest.model

import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Dao.BudgetDao
import com.android.walletforest.model.Dao.TransactionDao
import com.android.walletforest.model.Dao.WalletDao
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val walletDao: WalletDao,
    private val budgetDao: BudgetDao,
    private val walletMap: Map<Long, Wallet>,
    private val categoryMap: Map<Long, Category>
) {

    fun insertTransaction(transaction: Transaction) {
        GlobalScope.launch {
            val currentWallet = walletMap[transaction.walletId]!!.copy()
            transactionDao.insertTransaction(transaction)

            if (transaction.type == Constants.TYPE_EXPENSE)
                currentWallet.amount -= transaction.amount
            else
                currentWallet.amount += transaction.amount

            walletDao.updateWallet(currentWallet)


            updateBudget(transaction.categoryId, transaction, currentWallet.id)
            val category = categoryMap[transaction.categoryId]!!
            if (category.id != category.parentId)
                updateBudget(category.parentId, transaction, currentWallet.id)
        }
    }

    private suspend fun updateBudget(categoryId: Long, transaction: Transaction, walletId: Long) {
        val budget = budgetDao.getBudgetByCategorySync(categoryId, walletId)
        if (budget != null) {
            budget.spent += transaction.amount
            budgetDao.updateBudget(budget)
        }
    }

}