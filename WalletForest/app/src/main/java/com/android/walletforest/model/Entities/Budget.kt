package com.android.walletforest.model.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "budgets",
    foreignKeys = [ForeignKey(
    entity = Wallet::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("walletId"),
    onDelete = ForeignKey.CASCADE)])
data class Budget(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var categoryId: Long, var walletId: Long,
    var amount: Long, var spent: Long,
    var startDate: Long, var endDate: Long,
    var rangeDetail: String
)