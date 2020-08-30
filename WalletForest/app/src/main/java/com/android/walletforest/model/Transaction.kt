package com.android.walletforest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val categoryId:Long,
    val walletId:Long,
    var type:String,
    var amount:Long,
    var note:String,
    var time:Long
)
{
}