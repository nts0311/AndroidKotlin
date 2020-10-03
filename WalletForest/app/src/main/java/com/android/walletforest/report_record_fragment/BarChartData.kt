package com.android.walletforest.report_record_fragment

import java.io.Serializable

data class BarChartData(
    var xAxisLabel: String,
    var totalIncome: Long,
    var totalExpense: Long,
    var startDate: Long,
    var endDate: Long
) : Serializable