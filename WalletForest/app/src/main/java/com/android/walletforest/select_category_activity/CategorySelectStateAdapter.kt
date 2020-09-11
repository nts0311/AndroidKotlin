package com.android.walletforest.select_category_activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.walletforest.enums.Constants

class CategorySelectStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> CategoryListFragment.newInstance(Constants.TYPE_EXPENSE)
        1 -> CategoryListFragment.newInstance(Constants.TYPE_INCOME)
        else -> CategoryListFragment.newInstance(Constants.TYPE_EXPENSE)
    }
}