package com.android.walletforest.model.repositories

import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Dao.BudgetDao
import com.android.walletforest.model.Dao.TransactionDao
import com.android.walletforest.model.Dao.WalletDao
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.model.Entities.Wallet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val walletRepository: WalletRepository,
    private val budgetDao: BudgetDao,
    private val walletMap: Map<Long, Wallet>,
    private val categoryMap: Map<Long, Category>
) {
    //caching list of transactions of each wallet, avoiding database query
    private var fetchedRange: MutableMap<String, Flow<List<Transaction>>> = mutableMapOf()
    private val lock = Mutex()

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

    fun insertTransaction(transaction: Transaction) : Job =
        GlobalScope.launch {
            val currentWallet = walletMap[transaction.walletId]!!.copy()
            transactionDao.insertTransaction(transaction)

            if (transaction.type == Constants.TYPE_EXPENSE)
                currentWallet.amount -= transaction.amount
            else
                currentWallet.amount += transaction.amount

            walletRepository.updateWallet(currentWallet)


            updateBudget(
                transaction.categoryId,
                currentWallet.id,
                transaction.amount,
                transaction.date
            )
        }

    fun deleteTransaction(transaction: Transaction) : Job =
        GlobalScope.launch {
            val currentWallet = walletMap[transaction.walletId]!!.copy()
            transactionDao.deleteTransaction(transaction)

            if (transaction.type == Constants.TYPE_EXPENSE)
                currentWallet.amount += transaction.amount
            else
                currentWallet.amount -= transaction.amount

            walletRepository.updateWallet(currentWallet)

            updateBudget(
                transaction.categoryId,
                currentWallet.id,
                transaction.amount * -1,
                transaction.date
            )
        }

    fun updateTransaction(newTransaction: Transaction, oldTransaction: Transaction) {
        GlobalScope.launch {

            deleteTransaction(oldTransaction).join()
            insertTransaction(newTransaction).join()
        }
    }


    private suspend fun updateBudget(
        categoryId: Long,
        walletId: Long,
        diff: Long,
        transactionDate: Long
    ) {
        lock.withLock {
            //update the transaction's category budget (if it exists)
            val budget = budgetDao.getBudgetByCategorySync(categoryId, walletId, transactionDate)
            if (budget != null) {
                budget.spent += diff
                budgetDao.updateBudget(budget)
            }

            //update the transaction's parent category budget (if it exists)
            val category = categoryMap[categoryId]!!
            if (category.id != category.parentId) {
                val parentCategoryBudget =
                    budgetDao.getBudgetByCategorySync(category.parentId, walletId, transactionDate)
                if (parentCategoryBudget != null) {
                    parentCategoryBudget.spent += diff
                    budgetDao.updateBudget(parentCategoryBudget)
                }
            }

            //update the all-categories budget (if it exists)
            val allCategoryBudget = budgetDao.getAllCategoriesBudgetSync(walletId, transactionDate)
            if (allCategoryBudget != null) {
                allCategoryBudget.spent += diff
                budgetDao.updateBudget(allCategoryBudget)
            }
        }
    }
}