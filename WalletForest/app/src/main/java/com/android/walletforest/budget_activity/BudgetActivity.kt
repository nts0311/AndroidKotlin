package com.android.walletforest.budget_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.walletforest.R
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_budget.*

class BudgetActivity : AppCompatActivity() {

    private lateinit var budgetFragStateAdapter: BudgetFragStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        setUpViewPager2()
    }

    private fun setUpViewPager2()
    {
        budgetFragStateAdapter = BudgetFragStateAdapter(this)
        budget_veiw_pager.adapter = budgetFragStateAdapter

        TabLayoutMediator(tabLayout, budget_veiw_pager, true, true){ tab, position ->
            val tabTitle = if(position == 0) getString(R.string.running)
            else getString(R.string.ended)

            tab.text = tabTitle
        }.attach()
    }
}