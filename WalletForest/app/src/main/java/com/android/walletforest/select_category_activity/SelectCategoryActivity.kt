package com.android.walletforest.select_category_activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.walletforest.R
import com.android.walletforest.enums.Constants
import com.android.walletforest.model.Entities.Category
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_select_category.*
import kotlinx.android.synthetic.main.fragment_transactions.*

const val RESULT_CATEGORY_ID = "result_category_id"

class SelectCategoryActivity : AppCompatActivity() {

    private var viewPagerAdapter: CategorySelectStateAdapter? = null

    //when clicking on a category, return it's id as the result
    var categoryClickListener: (category: Category) -> Unit = { category ->
        val returnIntent = Intent()
        returnIntent.putExtra(RESULT_CATEGORY_ID, category.id)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_category)

        setupViewPager()
    }


    private fun setupViewPager() {
        viewPagerAdapter = CategorySelectStateAdapter(supportFragmentManager, lifecycle)
        viewPagerAdapter?.categoryClickListener = categoryClickListener
        category_viewpager.adapter = viewPagerAdapter
        category_type_tab.tabMode = TabLayout.MODE_FIXED

        TabLayoutMediator(category_type_tab, category_viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = Constants.TYPE_EXPENSE
                1 -> tab.text = Constants.TYPE_INCOME
            }
        }.attach()
    }
}