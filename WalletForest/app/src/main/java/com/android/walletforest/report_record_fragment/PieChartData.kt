package com.android.walletforest.report_record_fragment

import com.github.mikephil.charting.data.PieEntry
import java.io.Serializable

class PieChartData: Serializable {
    var incomePieEntries : List<PieEntry> = listOf()
    var expensePieEntries : List<PieEntry> = listOf()

    var incomeCategoryInfo : List<Pair<Long, Long>> = listOf()
    var expenseCategoryInfo : List<Pair<Long, Long>> = listOf()
}