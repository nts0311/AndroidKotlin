package com.android.walletforest.select_category_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Repository

class CategorySelectFragViewModel(repository: Repository) : ViewModel() {
    private val categoryType = MutableLiveData (Constants.TYPE_EXPENSE)
    val categories = Transformations.switchMap(categoryType){
        repository.getCategoriesByType(it)
    }

    fun setCategoryType(type: String)
    {
        categoryType.value = type
    }
}