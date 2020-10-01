package com.android.walletforest.report_record_fragment

data class BarChartData(
    var xAxisLabel: String,
    var totalIncome: Long,
    var totalExpense: Long,
    var startDate: Long,
    var endDate: Long
)