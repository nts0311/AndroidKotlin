package com.android.walletforest.TransactionListFragment

import com.android.walletforest.TransactionsFragment.TabInfoUtils
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DataGrouper {

    private var transactions: List<Transaction> = listOf()

    private var result: MutableList<DataItem> = mutableListOf()
    private var viewType: ViewType = ViewType.TRANSACTION

    suspend fun doGrouping(
        transactions: List<Transaction>,
        timeRange: TimeRange,
        viewType: ViewType
    ): List<DataItem> {
        this.transactions = transactions
        this.viewType = viewType

        if (viewType == ViewType.TRANSACTION) {
            when (timeRange) {
                TimeRange.WEEK, TimeRange.MONTH -> group { dividerItem, transaction ->
                    dividerItem.date.dayOfMonth == TabInfoUtils.toLocalDate(transaction.time).dayOfMonth
                }

                TimeRange.YEAR -> group { dividerItem, transaction ->
                    dividerItem.date.monthValue == TabInfoUtils.toLocalDate(transaction.time).monthValue
                }

                TimeRange.CUSTOM -> group { dividerItem, transaction ->
                    val transactionDate = TabInfoUtils.toLocalDate(transaction.time)
                    (dividerItem.date.monthValue == transactionDate.monthValue)
                            && (dividerItem.date.dayOfMonth == transactionDate.dayOfMonth)
                }
            }
        } else {
            group { dividerItem, transaction ->
                dividerItem.categoryId == transaction.categoryId
            }
        }

        return result
    }


    private suspend fun group(belongToGroup: (DataItem.DividerItem, Transaction) -> Boolean) {
        withContext(Dispatchers.Default)
        {
            result.clear()

            transactions = if (viewType == ViewType.TRANSACTION) {
                transactions.sortedWith { t1: Transaction, t2: Transaction ->

                    if (t1.time == t2.time)
                        (t2.id - t1.id).toInt()
                    else
                        (t2.time - t1.time).toInt()
                }
            } else
                transactions.sortedBy { it.categoryId }

            var totalAmount = 0L
            var numOfTransaction = 0

            var currentDividerItem = DataItem.DividerItem(
                TabInfoUtils.toLocalDate(transactions[0].time), transactions[0].categoryId,
                totalAmount, numOfTransaction
            )

            result.add(currentDividerItem)

            for (transaction in transactions) {
                if (belongToGroup(currentDividerItem, transaction)) {
                    result.add(DataItem.TransactionItem(transaction))

                    totalAmount += transaction.amount
                    numOfTransaction++

                    if (transactions.last() === transaction) {
                        currentDividerItem.numOfTransactions = numOfTransaction
                        currentDividerItem.totalAmount = totalAmount
                    }
                } else {
                    currentDividerItem.numOfTransactions = numOfTransaction
                    currentDividerItem.totalAmount = totalAmount

                    numOfTransaction = 1
                    totalAmount = transaction.amount

                    val dividerItem = DataItem.DividerItem(
                        TabInfoUtils.toLocalDate(transaction.time), transaction.categoryId,
                        totalAmount, numOfTransaction
                    )

                    currentDividerItem = dividerItem

                    result.add(dividerItem)
                    result.add(DataItem.TransactionItem(transaction))
                }
            }
        }
    }
}