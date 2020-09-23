package com.android.walletforest.report_fragment

import androidx.fragment.app.Fragment
import com.android.walletforest.report_record_fragment.ReportRecordFragment
import com.android.walletforest.viewpager2_fragment.TabFragmentStateAdapter

class ReportFragmentStateAdapter(fragment:Fragment) : TabFragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        val tabInfo = tabInfoList[position]
        return ReportRecordFragment.newInstance(
            tabInfo.startTime,
            tabInfo.endTime,
            tabInfo.walletId,
            timeRange
        )
    }
}