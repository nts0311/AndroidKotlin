package com.android.walletforest.model.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Wallet::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("walletId"),
            onDelete = ForeignKey.CASCADE))
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val categoryId: Long,
    val walletId: Long,
    var type: String,
    var amount: Long,
    var note: String,
    var time: Long
) {
}