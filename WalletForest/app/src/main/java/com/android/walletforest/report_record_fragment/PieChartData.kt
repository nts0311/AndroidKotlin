package com.android.walletforest.report_record_fragment

data class PieChartData(
    var incomePieList: List<Pair<Long, Long>>,
    var expensePieList: List<Pair<Long, Long>>
) {
}