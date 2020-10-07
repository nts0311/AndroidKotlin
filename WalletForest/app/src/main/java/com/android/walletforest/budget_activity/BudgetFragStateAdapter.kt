package com.android.walletforest.budget_activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.walletforest.budget_list_fragment.BUDGET_STATUS_ENDED
import com.android.walletforest.budget_list_fragment.BUDGET_STATUS_RUNNING
import com.android.walletforest.budget_list_fragment.BudgetListFragment

class BudgetFragStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val budgetsStatus = if(position == 0) BUDGET_STATUS_RUNNING
        else BUDGET_STATUS_ENDED

        return BudgetListFragment.newInstance(budgetsStatus)
    }
}