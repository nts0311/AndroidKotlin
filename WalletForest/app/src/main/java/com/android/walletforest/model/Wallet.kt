package com.android.walletforest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Wallet(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    var name:String,
    var amount:String
) {
}