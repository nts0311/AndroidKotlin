package com.android.walletforest.report_record_fragment

import androidx.lifecycle.*
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Repository
import com.github.mikephil.charting.data.BarEntry


class ReportRecordViewModel(private val repository: Repository) : ViewModel() {

    var barEntries : LiveData<List<BarEntry>> = MutableLiveData()
    var currentWallet = repository.currentWallet

    fun setTimeRange(startTime: Long, endTime: Long, timeRange: String, walletId: Long) {
        barEntries = repository.testFlow(startTime, endTime, walletId, TimeRange.valueOf(timeRange)).asLiveData()
    }
}