package com.android.walletforest.model.repositories

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


            updateBudget(transaction.categoryId, currentWallet.id, transaction.amount)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        GlobalScope.launch {
            val currentWallet = walletMap[transaction.walletId]!!.copy()
            transactionDao.deleteTransaction(transaction)

            if (transaction.type == Constants.TYPE_EXPENSE)
                currentWallet.amount += transaction.amount
            else
                currentWallet.amount -= transaction.amount

            walletDao.updateWallet(currentWallet)

            updateBudget(transaction.categoryId, currentWallet.id, transaction.amount * -1)
        }
    }

    fun updateTransaction(){

    }

    private suspend fun updateBudget(
        categoryId: Long,
        walletId: Long,
        diff: Long
    ) {
        val now = System.currentTimeMillis()

        //update the transaction's category budget (if it exists)
        val budget = budgetDao.getBudgetByCategorySync(categoryId, walletId)
        if (budget != null && budget.endDate > now) {
            budget.spent += diff
            budgetDao.updateBudget(budget)
        }

        //update the transaction's parent category budget (if it exists)
        val parentCategory = categoryMap[categoryId]!!
        if (parentCategory.id != parentCategory.parentId) {
            val parentCategoryBudget =
                budgetDao.getBudgetByCategorySync(parentCategory.parentId, walletId)
            if (parentCategoryBudget != null && parentCategoryBudget.endDate > now) {
                parentCategoryBudget.spent += diff
                budgetDao.updateBudget(parentCategoryBudget)
            }
        }

        //update the all-categories budget (if it exists)
        val allCategoryBudget = budgetDao.getAllCategoriesBudgetSync(walletId)
        if (allCategoryBudget != null && allCategoryBudget.endDate > now) {
            allCategoryBudget.spent += diff
            budgetDao.updateBudget(allCategoryBudget)
        }
    }
}