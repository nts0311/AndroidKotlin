package com.android.walletforest.TransactionsFragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.walletforest.TransactionListFragment.TransactionListFragment
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.main_activity.TabInfo

class TabFragmentStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    var tabInfoList: List<TabInfo> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var timeRange: TimeRange = TimeRange.MONTH
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = tabInfoList.size


    override fun createFragment(position: Int): Fragment {
        val tabInfo = tabInfoList[position]
        return TransactionListFragment.newInstance(
            tabInfo.startTime,
            tabInfo.endTime,
            tabInfo.walletId,
            timeRange
        )
    }
}