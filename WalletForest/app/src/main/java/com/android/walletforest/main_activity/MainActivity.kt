package com.android.walletforest.main_activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.*
import com.android.walletforest.TransactionsFragment.TransactionsFragment
import com.android.walletforest.transaction_detail_activity.TransactionDetailActivity
import com.android.walletforest.databinding.ActivityMainBinding
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Repository
import com.android.walletforest.report_fragment.ReportFragment
import com.android.walletforest.select_wallet_activity.RESULT_WALLET_ID
import com.android.walletforest.select_wallet_activity.SelectWalletActivity
import com.android.walletforest.viewpager2_fragment.ViewPager2Fragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    private val LAUNCH_WALLET_SELECT_ACTIVITY = 1

    private val fragmentTransactions = TransactionsFragment()
    private val fragmentReport = ReportFragment()
    private val fragmentPlanning = PlanningFragment()
    private var activeFragment: Fragment = fragmentTransactions
    protected var rangeSelectDialog: AlertDialog? = null

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
        transaction_frag_tab_layout.visibility = View.VISIBLE

        val vmFactory = RepoViewModelFactory(Repository.getInstance(this.applicationContext))
        viewModel = ViewModelProvider(this, vmFactory).get(MainActivityViewModel::class.java)


        setSupportActionBar(binding.toolbar)
        setUpBottomNav()
        registerObservers()
        registerClickListener()
        createDialogs()
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

            R.id.range_custom_item -> showRangeSelectDialog()
            R.id.switch_view_mode_item -> {
                val newViewMode = viewModel.switchViewMode()

                if (newViewMode == ViewType.TRANSACTION)
                    item.title = getString(R.string.view_by) + " " + getString(R.string.category)
                else
                    item.title = getString(R.string.view_by) + " " + getString(R.string.transaction)
            }

            R.id.edit_wallet_item -> {

            }
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LAUNCH_WALLET_SELECT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                val newWalletId = data?.getLongExtra(RESULT_WALLET_ID, 1)
                if (newWalletId != null) {
                    if (newWalletId != viewModel.currentWallet.value?.id)
                        viewModel.selectWallet(newWalletId)
                }
            }
        }
    }

    private fun setUpBottomNav() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.transactionsFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentTransactions).commit()
                    activeFragment = fragmentTransactions
                    report_frag_tab_layout.visibility = View.GONE
                    transaction_frag_tab_layout.visibility = View.VISIBLE
                    true
                }

                R.id.reportFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentReport).commit()
                    activeFragment = fragmentReport
                    report_frag_tab_layout.visibility = View.VISIBLE
                    transaction_frag_tab_layout.visibility = View.GONE
                    true
                }

                R.id.planningFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentPlanning).commit()
                    activeFragment = fragmentPlanning

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
            startActivityForResult(selectWalletActivity, LAUNCH_WALLET_SELECT_ACTIVITY)
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
                binding.walletBalance.text = it.amount.toString()
                binding.walletImg.setImageResource(it.imageId)

                if (!viewModel.initTabs) {
                    viewModel.initTabs = true
                    viewModel.getTabInfoList()
                }
            }
        }

        viewModel.walletList.observe(this)
        {
            if (it != null) {
                viewModel.updateWallets(it)

                if (!viewModel.initFirstWallet) {
                    if (it.isNotEmpty()) {
                        viewModel.selectWallet(it[0].id)
                        viewModel.initFirstWallet = true

                    } else {
                        //ask user to the create wallet
                    }
                }
            }
        }
    }

    private fun createDialogs() {
        rangeSelectDialog = AlertDialog.Builder(this).run {

            val inflater = layoutInflater

            val dialogView = inflater.inflate(R.layout.range_selection_dialog, null)

            val startEdt = dialogView.findViewById<EditText>(R.id.start_time_edt)
            val endEdt = dialogView.findViewById<EditText>(R.id.end_time_edt)

            setView(dialogView)
            setPositiveButton(R.string.select_time) { _, _ ->
                viewModel.onSelectCustomTimeRange(startEdt.tag as Long, endEdt.tag as Long)
            }

            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }

            create()
        }
    }

    private fun showRangeSelectDialog() {
        rangeSelectDialog?.show()

        val startDate = toLocalDate(viewModel.startTime)
        val endDate = toLocalDate(viewModel.endTime)

        val startEdt = rangeSelectDialog?.findViewById<EditText>(R.id.start_time_edt)
        startEdt?.setText(dateToString(startDate))
        startEdt?.tag = toEpoch(startDate)

        val endEdt = rangeSelectDialog?.findViewById<EditText>(R.id.end_time_edt)
        endEdt?.setText(dateToString(endDate))
        endEdt?.tag = toEpoch(endDate)

        val startDateSetListener: (DatePicker, Int, Int, Int) -> Unit =
            { _, year, monthOfYear, dayOfMonth ->
                val ld = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                startEdt?.setText(dateToString(ld))
                startEdt?.tag = toEpoch(ld)
            }

        val endDateSetListener: (DatePicker, Int, Int, Int) -> Unit =
            { _, year, monthOfYear, dayOfMonth ->
                val ld = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                endEdt?.setText(dateToString(ld))
                endEdt?.tag = toEpoch(ld)
            }

        startEdt?.setOnClickListener {
            DatePickerDialog(
                this, startDateSetListener,
                startDate.year, startDate.monthValue - 1, startDate.dayOfMonth
            ).show()
        }

        endEdt?.setOnClickListener {
            DatePickerDialog(
                this, endDateSetListener,
                endDate.year, endDate.monthValue - 1, endDate.dayOfMonth
            ).show()
        }

    }

    fun getTabLayout(fragment: ViewPager2Fragment): TabLayout =
        if (fragment is TransactionsFragment)
            transaction_frag_tab_layout
        else
            report_frag_tab_layout
}