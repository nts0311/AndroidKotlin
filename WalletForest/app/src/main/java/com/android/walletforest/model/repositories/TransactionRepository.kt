package com.android.walletforest.model.repositories

import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Dao.BudgetDao
import com.android.walletforest.model.Dao.TransactionDao
import com.android.walletforest.model.Dao.WalletDao
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val walletDao: WalletDao,
    private val budgetDao: BudgetDao,
    private val walletMap: Map<Long, Wallet>,
    private val categoryMap: Map<Long, Category>
) {
    //caching list of transactions of each wallet, avoiding database query
    private var fetchedRange: MutableMap<String, Flow<List<Transaction>>> = mutableMapOf()

    //Get the list of transactions in a specific wallet and specific period
    //If the list is not cached, fetch the list from database and cache it
    fun getTransactionsBetweenRange(
        start: Long,
        end: Long,
        walletId: Long
    ): Flow<List<Transaction>> {

        val key: String = if (walletId == 1L)
            "all-$start-$end"
        else
            "$walletId-$start-$end"

        return if (fetchedRange.containsKey(key))
            fetchedRange[key]!!
        else {

            val transactions =
                if (walletId == 1L)
                    transactionDao.getTransactionsBetweenRange(start, end)
                else
                    transactionDao.getTransactionsBetweenRangeOfWallet(
                        start,
                        end,
                        walletId
                    )

            fetchedRange[key] = transactions.distinctUntilChanged()

            transactions
        }
    }

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

    fun updateTransaction(newTransaction: Transaction, oldTransaction: Transaction) {

        GlobalScope.launch {
            val currentWallet = walletMap[newTransaction.walletId]!!.copy()
            transactionDao.updateTransaction(newTransaction)


            if (newTransaction.type == oldTransaction.type) {
                if (newTransaction.type == Constants.TYPE_EXPENSE)
                    currentWallet.amount += (oldTransaction.amount - newTransaction.amount)
                else
                    currentWallet.amount -= (oldTransaction.amount - newTransaction.amount)
            } else {
                if (newTransaction.type == Constants.TYPE_EXPENSE)
                    currentWallet.amount -= (oldTransaction.amount + newTransaction.amount)
                else
                    currentWallet.amount += (oldTransaction.amount + newTransaction.amount)
            }

            walletDao.updateWallet(currentWallet)


            //update budget

            if (newTransaction.type == Constants.TYPE_INCOME && oldTransaction.type == Constants.TYPE_EXPENSE)
                updateBudget(
                    oldTransaction.categoryId,
                    oldTransaction.walletId,
                    oldTransaction.amount * -1
                )
            else if (newTransaction.type == Constants.TYPE_EXPENSE && oldTransaction.type == Constants.TYPE_INCOME)
                updateBudget(
                    newTransaction.categoryId,
                    newTransaction.walletId,
                    newTransaction.amount
                )
            else if (newTransaction.type == Constants.TYPE_EXPENSE && oldTransaction.type == Constants.TYPE_EXPENSE) {
                if (newTransaction.categoryId == oldTransaction.categoryId) {
                    val diff = newTransaction.amount - oldTransaction.amount
                    updateBudget(newTransaction.categoryId, newTransaction.walletId, diff)
                } else {
                    updateBudget(
                        oldTransaction.categoryId,
                        oldTransaction.walletId,
                        oldTransaction.amount * -1
                    )
                    updateBudget(
                        newTransaction.categoryId,
                        newTransaction.walletId,
                        newTransaction.amount
                    )
                }
            }
        }
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