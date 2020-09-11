package com.android.walletforest.model.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.walletforest.model.Entities.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun getCategories():LiveData<List<Category>>

    @Query("SELECT * FROM category WHERE type = :type ORDER BY parentId ASC, id ASC")
    fun getCategoriesByType(type: String): LiveData<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)
}