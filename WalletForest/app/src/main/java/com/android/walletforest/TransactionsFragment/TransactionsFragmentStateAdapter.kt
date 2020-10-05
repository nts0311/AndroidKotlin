package com.android.walletforest.TransactionsFragment

import androidx.fragment.app.Fragment
import com.android.walletforest.TransactionListFragment.TransactionListFragment
import com.android.walletforest.pie_chart_detail_activity.PieChartRangeParams
import com.android.walletforest.viewpager2_fragment.TabFragmentStateAdapter

class TransactionsFragmentStateAdapter(fragment: Fragment) : TabFragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        val tabInfo = tabInfoList[position]
        return TransactionListFragment.newInstance(
            tabInfo.startTime,
            tabInfo.endTime,
            tabInfo.walletId,
            timeRange,
            PieChartRangeParams(-1L,"",false) //ignore it
        )
    }
}