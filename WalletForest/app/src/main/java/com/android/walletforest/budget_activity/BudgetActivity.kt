package com.android.walletforest.budget_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.main_activity.MainActivityViewModel
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.select_wallet_activity.SelectWalletActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_budget.*

class BudgetActivity : AppCompatActivity() {

    private lateinit var budgetFragStateAdapter: BudgetFragStateAdapter
    private lateinit var viewModel: BudgetActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        val vmFactory = RepoViewModelFactory(Repository.getInstance(this.applicationContext))
        viewModel = ViewModelProvider(this, vmFactory).get(BudgetActivityViewModel::class.java)

        setUpViewPager2()

        wallet_icon.setOnClickListener {
            val selectWalletActivity = Intent(this@BudgetActivity, SelectWalletActivity::class.java)
            startActivity(selectWalletActivity,)
        }

        viewModel.currentWallet.observe(this)
        {
            wallet_icon.setImageResource(it.imageId)
        }
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