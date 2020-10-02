package com.android.walletforest.report_record_fragment

import androidx.lifecycle.*
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Repository
import com.github.mikephil.charting.data.BarData


class ReportRecordViewModel(private val repository: Repository) : ViewModel() {

    var barData : LiveData<Pair<List<BarChartData>, PieChartData>> = MutableLiveData()
    var currentWallet = repository.currentWallet

    fun setTimeRange(startTime: Long, endTime: Long, timeRange: String, walletId: Long) {
        barData = repository.getBarData(startTime, endTime, walletId, TimeRange.valueOf(timeRange)).asLiveData()
    }

    fun getCateImageId(categoryId: Long): Int  = repository.getCategoryImage(categoryId)

}