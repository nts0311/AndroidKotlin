package com.android.walletforest.model.Entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.walletforest.enums.Constants

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    var id:Long,
    var parentId :Long,
    var name: String,
    var type : String = Constants.TYPE_EXPENSE,
    var imageId:Int
) {
}