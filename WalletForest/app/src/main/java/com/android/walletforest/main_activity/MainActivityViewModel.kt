package com.android.walletforest.main_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.utils.toEpoch
import com.android.walletforest.utils.toLocalDate
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivityViewModel(private val repository: Repository) : ViewModel() {
    var categoryList = repository.getCategoriesLiveData()
    var walletList = repository.walletList

    var startTime: Long = 0L
        private set
    var endTime: Long = System.currentTimeMillis()
        private set

    var currentWallet: LiveData<Wallet> = repository.currentWallet

    private var timeRange = TimeRange.MONTH

    //determine if the tabs has been initialized and displayed
    var initTabs = false
    //var initFirstWallet = false

    init {
        initTimeRange()
        repository.setCurrentWallet(1L)
    }

    private fun initTimeRange() {

        val ld = toLocalDate(endTime)

        startTime = toEpoch(
            LocalDate.of(ld.year, ld.monthValue, 1)
                .minusMonths(18)
        )
    }

    fun updateCategories(categories: List<Category>) {
        repository.updateCategoriesMap(categories)
    }

    /*fun selectWallet(walletId: Long) {
        repository.setCurrentWallet(walletId)
    }*/

    fun getTabInfoList() {
        if (currentWallet.value == null)
            return

        viewModelScope.launch {
            val tabInfoList = TabInfoUtils.getTabInfoList(startTime,endTime,timeRange,currentWallet.value!!.id)
                .first()
            repository.setTabInfoList(tabInfoList)
        }
    }

    fun onSelectCustomTimeRange(start: Long, end: Long) {
        if (startTime == start && endTime == end)
            return

        startTime = start
        endTime = end
        timeRange = TimeRange.CUSTOM
        repository.setTimeRange(TimeRange.CUSTOM)
        getTabInfoList()
    }

    //not include CUSTOM range
    fun onTimeRangeChanged(timeRange: TimeRange) {
        if (timeRange.value == this.timeRange.value)
            return

        if (this.timeRange == TimeRange.CUSTOM && timeRange != TimeRange.CUSTOM)
            initTimeRange()

        this.timeRange = timeRange
        repository.setTimeRange(timeRange)
        getTabInfoList()
    }

    fun switchViewMode(): ViewType {
        val currentViewMode = repository.viewMode

        val newViewMode = if (currentViewMode.value == ViewType.TRANSACTION)
            ViewType.CATEGORY
        else
            ViewType.TRANSACTION

        currentViewMode.value = newViewMode
        return newViewMode
    }
}