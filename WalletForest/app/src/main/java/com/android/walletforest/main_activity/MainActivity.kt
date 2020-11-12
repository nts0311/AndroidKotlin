package com.android.walletforest.main_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.*
import com.android.walletforest.TransactionsFragment.TransactionsFragment
import com.android.walletforest.transaction_detail_activity.TransactionDetailActivity
import com.android.walletforest.databinding.ActivityMainBinding
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.planning_fragment.PlanningFragment
import com.android.walletforest.report_fragment.ReportFragment
import com.android.walletforest.select_wallet_activity.SelectWalletActivity
import com.android.walletforest.utils.NumberFormatter
import com.android.walletforest.utils.createRangeSelectDialog
import com.android.walletforest.utils.showRangeSelectDialog
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var fragmentTransactions = TransactionsFragment()
    private var fragmentReport = ReportFragment()
    private var fragmentPlanning = PlanningFragment()
    private var activeFragment: Fragment = fragmentTransactions
    private var rangeSelectDialog: AlertDialog? = null

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportFragmentManager.apply {
            beginTransaction().add(R.id.fragment_container, fragmentPlanning, "frag_planning")
                .hide(fragmentPlanning).commit()
            beginTransaction().add(R.id.fragment_container, fragmentReport, "frag_report")
                .hide(fragmentReport).commit()
            beginTransaction().add(
                R.id.fragment_container,
                fragmentTransactions,
                "frag_transactions"
            )
                .commit()
        }

        val vmFactory = RepoViewModelFactory(Repository.getInstance(this.applicationContext))
        viewModel = ViewModelProvider(this, vmFactory).get(MainActivityViewModel::class.java)


        setSupportActionBar(binding.toolbar)
        setUpBottomNav()
        registerObservers()
        registerClickListener()

        rangeSelectDialog = createRangeSelectDialog(this) { startDate, endDate ->
            viewModel.onSelectCustomTimeRange(startDate, endDate)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.transactions_frag_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.range_month_item -> {
                viewModel.onTimeRangeChanged(TimeRange.MONTH)
            }
            R.id.range_week_item -> {
                viewModel.onTimeRangeChanged(TimeRange.WEEK)
            }
            R.id.range_year_item -> {
                viewModel.onTimeRangeChanged(TimeRange.YEAR)
            }

            R.id.range_custom_item -> showRangeSelectDialog(
                this,
                rangeSelectDialog,
                viewModel.startTime,
                viewModel.endTime
            )
            R.id.switch_view_mode_item -> {
                val newViewMode = viewModel.switchViewMode()

                if (newViewMode == ViewType.TRANSACTION)
                    item.title = getString(R.string.view_by) + " " + getString(R.string.category)
                else
                    item.title = getString(R.string.view_by) + " " + getString(R.string.transaction)
            }

            R.id.edit_wallet_item -> {

            }

            R.id.change_pie_mode -> return false
        }

        return true
    }

    private fun setUpBottomNav() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.transactionsFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentTransactions).commit()
                    activeFragment = fragmentTransactions
                    if (binding.tabLayout.visibility == View.GONE)
                        binding.tabLayout.visibility = View.VISIBLE
                    true
                }

                R.id.reportFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentReport).commit()
                    activeFragment = fragmentReport
                    if (binding.tabLayout.visibility == View.GONE)
                        binding.tabLayout.visibility = View.VISIBLE
                    true
                }

                R.id.planningFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentPlanning).commit()
                    activeFragment = fragmentPlanning
                    if (binding.tabLayout.visibility == View.VISIBLE)
                        binding.tabLayout.visibility = View.GONE
                    true
                }

                else -> false
            }
        }
    }

    private fun registerClickListener() {
        //add transaction
        binding.addTransactionFab.setOnClickListener {
            val addTransaction = Intent(this@MainActivity, TransactionDetailActivity::class.java)
            addTransaction.putExtra(
                TransactionDetailActivity.WALLET_ID_PARAM,
                viewModel.currentWallet.value?.id
            )
            startActivity(addTransaction)
        }

        //select wallet
        binding.walletImg.setOnClickListener {
            val selectWalletActivity = Intent(this@MainActivity, SelectWalletActivity::class.java)
            startActivity(selectWalletActivity)
        }
    }

    private fun registerObservers() {
        viewModel.categoryList.observe(this)
        {
            if (it != null) {
                viewModel.updateCategories(it)
            }
        }

        viewModel.currentWallet.observe(this)
        {
            if (it != null) {
                binding.walletName.text = it.name
                binding.walletBalance.text = NumberFormatter.format(it.amount) + " " + it.currency
                binding.walletImg.setImageResource(it.imageId)

                if (!viewModel.initTabs) {
                    viewModel.initTabs = true
                    viewModel.getTabInfoList()
                }
            }
        }
    }

    fun getTabLayout(): TabLayout = binding.tabLayout
}