package com.android.walletforest.report_record_fragment

import android.util.Log
import androidx.lifecycle.*
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Repository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class ReportRecordViewModel(private val repository: Repository) : ViewModel() {

    var barData: LiveData<Pair<List<BarChartData>, PieChartData>> = MutableLiveData()
    var pieEntries: LiveData<Pair<List<PieEntry>, List<PieEntry>>> = MutableLiveData()

    var currentWallet = repository.currentWallet
    var excludeSubCate = true

    fun setTimeRange(startTime: Long, endTime: Long, timeRange: String, walletId: Long) {
        barData = repository.getBarData(startTime, endTime, walletId, TimeRange.valueOf(timeRange))
            .asLiveData()
    }

    fun getPieEntries(startTime: Long, endTime: Long, timeRange: String, walletId: Long) {
        pieEntries =
            repository.getPieEntries(startTime, endTime, walletId, excludeSubCate).asLiveData()
    }
}