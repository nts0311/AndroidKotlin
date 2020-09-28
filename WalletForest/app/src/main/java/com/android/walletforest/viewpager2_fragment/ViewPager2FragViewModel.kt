package com.android.walletforest.viewpager2_fragment

import androidx.lifecycle.*
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Repository

open class ViewPager2FragViewModel(val repository: Repository) : ViewModel() {
    var tabInfoList = repository.tabInfoList
    var timeRange = repository.timeRange

    fun setViewPagerPage(page : Int)
    {
        repository.currentPage = page
    }

    fun getCurrentPage(): Int = repository.currentPage
}