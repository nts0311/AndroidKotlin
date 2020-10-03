package com.android.walletforest.bar_chart_detail_activity

import androidx.lifecycle.ViewModel
import com.android.walletforest.model.Repository
import com.android.walletforest.report_record_fragment.BarChartData
import com.github.mikephil.charting.data.BarEntry

class BarChartDetailViewModel(private val repository: Repository) : ViewModel() {
    var barDataList : List<BarChartData> = listOf()
    var barEntries : List<BarEntry> = listOf()
}