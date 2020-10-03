package com.android.walletforest.report_record_fragment

import androidx.lifecycle.*
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Repository
import com.github.mikephil.charting.data.PieEntry


class ReportRecordViewModel(private val repository: Repository) : ViewModel() {

    var barData: LiveData<List<BarChartData>> = MutableLiveData()
    var pieEntries: LiveData<Pair<List<PieEntry>, List<PieEntry>>> = MutableLiveData()

    var currentWallet = repository.currentWallet
    var excludeSubCate = false

    fun setTimeRange(startTime: Long, endTime: Long, timeRange: String, walletId: Long) {
        barData = repository.getBarData(startTime, endTime, walletId, TimeRange.valueOf(timeRange)).asLiveData()
    }

    fun getPieEntries(startTime: Long, endTime: Long, walletId: Long) {
        pieEntries =
            repository.getPieEntries(startTime, endTime, walletId, excludeSubCate).asLiveData()
    }
}