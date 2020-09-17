package com.android.walletforest.main_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Entities.Category
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository
import com.android.walletforest.toEpoch
import com.android.walletforest.toLocalDate
import kotlinx.coroutines.async
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

    private val tabInfoUtils = TabInfoUtils()
    private var timeRange = TimeRange.MONTH

    //determine if the tabs has been initialized and displayed
    var initTabs = false
    var initFirstWallet = false

    init {
        initTimeRange()
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

    fun updateWallets(wallets: List<Wallet>) {
        repository.updateWalletsMap(wallets)
    }

    fun selectWallet(walletId: Long) {
        repository.setCurrentWallet(walletId)
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


    fun getTabInfoList() {
        if (currentWallet.value == null)
            return

        tabInfoUtils.setProperties(startTime, endTime, timeRange, currentWallet.value!!.id)

        viewModelScope.launch {
            val result = async {
                tabInfoUtils.getTabInfoList()
            }
            repository.setTabInfoList(result.await())
        }
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