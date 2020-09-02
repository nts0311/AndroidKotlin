package com.android.walletforest.TransactionListFragment

import com.android.walletforest.enums.ItemType
import com.android.walletforest.model.Entities.Transaction
import java.time.LocalDate

sealed class DataItem {
    abstract var itemType:ItemType

    data class TransactionItem(val transaction: Transaction) : DataItem()
    {
        override var itemType = ItemType.TRANSACTION
    }

    data class DividerItem(
        var date: LocalDate,
        var categoryId: Long,
        var totalAmount: Long,
        var numOfTransactions:Int
    ): DataItem()
    {
        override var itemType = ItemType.DIVIDER
    }

    data class HeaderItem(
        var openingBalance:Long,
        var endingBalance:Long
    ):DataItem()
    {
        override var itemType = ItemType.HEADER
    }
}