package com.android.walletforest.main_activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.*
import com.android.walletforest.TransactionsFragment.TransactionsFragment
import com.android.walletforest.databinding.ActivityMainBinding
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.enums.ViewType
import com.android.walletforest.model.Repository
import com.android.walletforest.select_category_activity.SelectCategoryActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    private val fragmentTransactions = TransactionsFragment()
    private val fragmentReport = ReportFragment()
    private val fragmentPlanning = PlanningFragment()
    private var activeFragment: Fragment = fragmentTransactions
    private var rangeSelectDialog: AlertDialog? = null

    private lateinit var binding : ActivityMainBinding

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

        setUpBottomNav()
        registerObservers()
        createDialogs()
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.transactions_frag_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
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

            R.id.edit_wallet_item ->
            {

            }
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
                    true
                }

                R.id.reportFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(fragmentReport).commit()
                    activeFragment = fragmentReport
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

    private fun registerObservers() {
        viewModel.categoryList.observe(this)
        {
            if (it != null) {
                viewModel.updateCategories(it)
            }
        }

        viewModel.walletList.observe(this)
        {
            if (it != null) {
                viewModel.updateWallets(it)
            }
        }

        viewModel.currentWallet.observe(this)
        {
            if (it != null) {
                binding.walletName.text = it.name
                binding.walletBalance.text = it.amount.toString()
                viewModel.onCurrentWalletChange()
            }
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

    private fun createDialogs() {
        rangeSelectDialog = AlertDialog.Builder(this).run {

            val inflater = layoutInflater

            val dialogView = inflater.inflate(R.layout.range_selection_dialog, null)

            val startEdt = dialogView.findViewById<EditText>(R.id.start_time_edt)
            val endEdt = dialogView.findViewById<EditText>(R.id.end_time_edt)

            setView(dialogView)
            setPositiveButton(R.string.select_time) { dialog, id ->
                viewModel.onSelectCustomTimeRange(startEdt.tag as Long, endEdt.tag as Long)
            }

            setNegativeButton(R.string.cancel) { dialog, id ->
                dialog.cancel()
            }

            create()
        }
    }

    fun getTabLayout() : TabLayout = binding.tabLayout
}