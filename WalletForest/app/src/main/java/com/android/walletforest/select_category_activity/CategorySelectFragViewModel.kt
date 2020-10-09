package com.android.walletforest.select_category_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.repositories.Repository

class CategorySelectFragViewModel(repository: Repository) : ViewModel() {
    private val categoryType = MutableLiveData(Constants.TYPE_EXPENSE)
    val categories = Transformations.switchMap(categoryType) {
        repository.getCategoriesByType(it)
    }
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    fun setCategoryType(type: String) {
        categoryType.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}