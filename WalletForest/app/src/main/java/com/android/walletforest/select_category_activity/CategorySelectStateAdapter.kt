package com.android.walletforest.select_category_activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Category

class CategorySelectStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    var categoryClickListener: (category: Category) -> Unit = {}

    var addAllCategory = false

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val frag = when (position) {
            0 -> CategoryListFragment.newInstance(Constants.TYPE_EXPENSE, addAllCategory)
            1 -> CategoryListFragment.newInstance(Constants.TYPE_INCOME)
            else -> CategoryListFragment.newInstance(Constants.TYPE_EXPENSE)
        }
        frag.categoryClickListener = categoryClickListener
        return frag
    }
}