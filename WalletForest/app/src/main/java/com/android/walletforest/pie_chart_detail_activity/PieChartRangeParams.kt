package com.android.walletforest.pie_chart_detail_activity

import java.io.Serializable

class PieChartRangeParams(
    var categoryIdToFilter: Long = -1L,
    var transactionType: String? = "",
    var excludeSubCate: Boolean = false
) : Serializable