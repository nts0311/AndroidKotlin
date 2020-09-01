package com.android.walletforest.model.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = Wallet::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("walletId"),
        onDelete = ForeignKey.CASCADE)]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var categoryId: Long,
    var walletId: Long,
    var type: String,
    var amount: Long,
    var note: String,
    var time: Long
) {
}