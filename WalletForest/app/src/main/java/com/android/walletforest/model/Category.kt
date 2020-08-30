package com.android.walletforest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val parentId :Long,
    var name: String,
    var imageId:Int
) {
}