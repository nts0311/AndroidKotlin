package com.android.walletforest.viewpager2_fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.walletforest.TransactionListFragment.TransactionListFragment
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.main_activity.TabInfo

abstract class TabFragmentStateAdapter(fragment : Fragment) :
    FragmentStateAdapter(fragment) {

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
}