package com.android.walletforest.report_record_fragment

import android.util.Log
import androidx.lifecycle.*
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Repository
import com.github.mikephil.charting.data.BarData
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch


class ReportRecordViewModel(private val repository: Repository) : ViewModel() {

    var barData : LiveData<Pair<List<BarChartData>, PieChartData>> = MutableLiveData()
    var currentWallet = repository.currentWallet

    private var flow : Flow<Pair<List<BarChartData>, PieChartData>>? = null

    fun setTimeRange(startTime: Long, endTime: Long, timeRange: String, walletId: Long) {
        flow = repository.getBarData(startTime, endTime, walletId, TimeRange.valueOf(timeRange))
        barData = repository.getBarData(startTime, endTime, walletId, TimeRange.valueOf(timeRange)).asLiveData()
    }

    fun getCateImageId(categoryId: Long): Int  = repository.getCategoryImage(categoryId)
    @InternalCoroutinesApi
    fun test() {
        viewModelScope.launch {
            flow?.collect{pair->
                Log.i("aaa", it)
            }
        }
    }

}