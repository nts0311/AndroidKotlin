package com.android.walletforest.model.Entities

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