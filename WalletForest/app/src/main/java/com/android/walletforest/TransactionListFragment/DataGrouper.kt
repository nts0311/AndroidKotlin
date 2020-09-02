package com.android.walletforest.TransactionListFragment

import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Entities.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DataGrouper {

    private var transactions: List<Transaction> = listOf()
    private var timeRange = TimeRange.MONTH

    private val result: MutableList<DataItem> = mutableListOf()


    private suspend fun groupByTime() {
        withContext(Dispatchers.Default)
        {
            transactions.sortedByDescending { it.time }

            var totalAmount = 0L
            var numOfTransaction = 0

            var currentDate = LocalDate.ofEpochDay(transactions[0].time)
            var currentDividerItem = DataItem.DividerItem(
                transactions[0].time, transactions[0].categoryId, totalAmount, numOfTransaction
            )

            result.add(currentDividerItem)

            for (transaction in transactions) {
                if (LocalDate.ofEpochDay(transaction.time).dayOfMonth
                    == currentDate.dayOfMonth
                ) {
                    result.add(DataItem.TransactionItem(transaction))
                    totalAmount += transaction.amount
                    numOfTransaction++
                } else {
                    currentDividerItem.numOfTransactions = numOfTransaction
                    currentDividerItem.totalAmount = totalAmount

                    numOfTransaction = 1
                    totalAmount = transaction.amount
                    currentDate = LocalDate.ofEpochDay(transaction.time)

                    val dividerItem = DataItem.DividerItem(
                        transaction.time, transaction.categoryId, totalAmount, numOfTransaction
                    )

                    currentDividerItem = dividerItem

                    result.add(dividerItem)
                    result.add(DataItem.TransactionItem(transaction))
                }
            }
        }
    }
}