package com.android.walletforest.select_budget_range

import java.io.Serializable

data class BudgetRange (
    var title: String = "",
    var rangeDetail: String = "",
    var startDate: Long = 0L,
    var endDate: Long = 0L
) : Serializable