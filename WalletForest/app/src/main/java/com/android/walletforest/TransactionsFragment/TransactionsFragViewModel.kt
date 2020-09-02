package com.android.walletforest.TransactionsFragment

import androidx.lifecycle.*
import com.android.walletforest.model.Entities.Wallet
import com.android.walletforest.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.android.walletforest.enums.TimeRange
import kotlinx.coroutines.async
import java.time.LocalDate

class TransactionsFragViewModel(private val repository: Repository) : ViewModel() {

    var currentWallet: LiveData<Wallet> = repository.getFirstWallet()


    var startTime: Long = 0L
        private set
    var endTime: Long = System.currentTimeMillis()
        private set

    private var _tabInfoList = MutableLiveData<List<TabInfo>>()
    private val tabInfoUtils = TabInfoUtils()
    private var timeRange = TimeRange.MONTH


    var tabInfoList: LiveData<List<TabInfo>> = _tabInfoList

    init {
        initTimeRange()
    }

    private fun initTimeRange() {

        val ld = TabInfoUtils.toLocalDate(endTime)
       /* endTime = TabInfoUtils.toEpoch(
            LocalDate.of(ld.year, ld.monthValue + 1, 1)
                .minusDays(1)
        )*/

        startTime = TabInfoUtils.toEpoch(
            LocalDate.of(ld.year, ld.monthValue, 1)
                .minusMonths(18)
        )

    }


    fun onCurrentWalletChange() {
        getTabInfoList()
    }

    fun onSelectCustomTimeRange(start: Long, end: Long) {
        if (startTime == start && endTime == end)
            return

        startTime = start
        endTime = end
        timeRange = TimeRange.CUSTOM
        getTabInfoList()
    }

    //not include CUSTOM range
    fun onTimeRangeChanged(timeRange: TimeRange) {
        if (timeRange.value == this.timeRange.value)
            return
        this.timeRange = timeRange
        getTabInfoList()
    }

    private fun getTabInfoList() {
        if (currentWallet.value == null)
            return

        tabInfoUtils.setProperties(startTime, endTime, timeRange, currentWallet.value!!.id)

        viewModelScope.launch {
            val result = async {
                tabInfoUtils.getTabInfoList()
            }
            _tabInfoList.value = result.await()
        }
    }

}