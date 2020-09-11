package com.android.walletforest.select_category_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.walletforest.R
import com.android.walletforest.enums.Constants
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_transactions.*

class SelectCategoryActivity : AppCompatActivity() {

    private var viewPagerAdapter: CategorySelectStateAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_category)

        setupViewPager()
    }

    fun setupViewPager() {
        viewPagerAdapter = CategorySelectStateAdapter(supportFragmentManager, lifecycle)
        main_view_pager.adapter = viewPagerAdapter
        tab_layout.tabMode = TabLayout.MODE_FIXED

        TabLayoutMediator(tab_layout, main_view_pager) { _, position ->
            when (position) {
                0 -> Constants.TYPE_EXPENSE
                1 -> Constants.TYPE_INCOME
            }
        }.attach()
    }
}