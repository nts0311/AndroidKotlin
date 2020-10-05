package com.android.walletforest.report_record_fragment

import android.os.Parcel
import android.os.Parcelable
import com.github.mikephil.charting.data.PieEntry
import java.io.Serializable

class PieChartData{
    var incomePieEntries : List<PieEntry> = listOf()
    var expensePieEntries : List<PieEntry> = listOf()

    var incomeCategoryInfo : List<Pair<Long, Long>> = listOf()
    var expenseCategoryInfo : List<Pair<Long, Long>> = listOf()
}