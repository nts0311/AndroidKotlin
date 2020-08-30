package com.android.walletforest.model.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.android.walletforest.model.Entities.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun getCategories():LiveData<List<Category>>

}