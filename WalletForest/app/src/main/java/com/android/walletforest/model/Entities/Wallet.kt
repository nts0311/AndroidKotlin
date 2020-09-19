package com.android.walletforest.model.Entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Wallet(
    @PrimaryKey(autoGenerate = true)
    var id:Long,
    var name:String,
    var imageId:Int,
    var amount: Long,
    var currency: String
) {

}