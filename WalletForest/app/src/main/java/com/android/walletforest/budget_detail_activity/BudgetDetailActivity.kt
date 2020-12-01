package com.android.walletforest.budget_detail_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.TransactionListFragment.*
import com.android.walletforest.add_budget_activity.AddBudgetActivity
import com.android.walletforest.enums.Constants
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Entities.Budget
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.pie_chart_detail_activity.FilteringParams
import com.android.walletforest.transaction_list_activity.TransactionListActivity
import com.android.walletforest.utils.NumberFormatter
import com.android.walletforest.utils.dateToString
import com.android.walletforest.utils.setProgressProgressBar
import com.android.walletforest.utils.toLocalDate
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.xxmassdeveloper.mpchartexample.custom.com.android.walletforest.MyMarkerView
import kotlinx.android.synthetic.main.activity_budget_detail.*
import kotlin.math.abs
import kotlin.math.max

class BudgetDetailActivity : AppCompatActivity() {

    companion object {
        val BUDGET_ID_KEY = "budgetId"
    }

    private lateinit var viewModel: BudgetDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_detail)

        viewModel = ViewModelProvider(
            this,
            RepoViewModelFactory(Repository.getInstance(applicationContext))
        ).get(BudgetDetailViewModel::class.java)

        viewModel.budgetId = intent.getLongExtra(BUDGET_ID_KEY, 0)

        setupChart(budget_chart)



        setSupportActionBar(budget_detail_toolbar)
        registerObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.budget_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item_edit_budget -> {
                val editBudgetIntent = Intent(this, AddBudgetActivity::class.java)
                editBudgetIntent.putExtra(AddBudgetActivity.BUDGET_ID, viewModel.budgetId)
                startActivity(editBudgetIntent)
                true
            }
            R.id.item_delete_budget -> {
                viewModel.deleteBudget(viewModel.currentBudget.value!!)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupChart(chart: LineChart) {
        chart.apply {
            legend.isEnabled = false
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)

            xAxis.setDrawAxisLine(false)
            description.isEnabled = false

            xAxis.axisMinimum = 0.0f
            //xAxis.setDrawLabels(false)

            axisLeft.valueFormatter = LargeValueFormatter().apply {
                setSuffix(arrayOf("", "K", "M", "B", "T"))
            }

            // create marker to display box when values are selected
            val mv = MyMarkerView(this@BudgetDetailActivity, R.layout.custom_marker)
            mv.chartView = this
            marker = mv
        }
    }


    @SuppressLint("SetTextI18n")
    private fun registerObservers() {
        viewModel.currentBudget.observe(this)
        {
            if (it==null)
                return@observe

            viewModel.setBudgetInfo(it)
            drawLimitLine(it.amount)
            populateBudgetUI(it)
            setViewTransactionsListBtn(it)
        }

        viewModel.lineEntries.observe(this)
        {
            drawChart(it)
        }

        viewModel.dayRemaining.observe(this)
        {
            day_left_txt.text = it.toString() + getString(R.string.days_left)
        }

        viewModel.recommendDailySpending.observe(this)
        {
            recommend_daily_spending_txt.text = NumberFormatter.format(it)
        }

        viewModel.projectedSpending.observe(this)
        {
            projected_spending_txt.text = NumberFormatter.format(it)
        }

        viewModel.actualDailySpending.observe(this)
        {
            actual_daily_spending_txt.text = NumberFormatter.format(it)
        }
    }

    private fun populateBudgetUI(budget : Budget)
    {
        if (budget.categoryId==-1L) {
            category_img.setImageResource(R.drawable.ic_category_all)
            category_txt.text = applicationContext.getString(R.string.all_categories)
        } else {
            val category = viewModel.categoryMap[budget.categoryId]!!
            category_img.setImageResource(category.imageId)
            category_txt.text = category.name
        }

        amount_txt.text = NumberFormatter.format(budget.amount)
        amount_spent_txt.text = NumberFormatter.format(budget.spent)

        if(budget.amount >= budget.spent)
            left_overspent_txt.text = getString(R.string.left)
        else
            left_overspent_txt.text = getString(R.string.overspent)

        amount_left_txt.text = NumberFormatter.format(abs(budget.amount - budget.spent))

        val wallet = viewModel.walletMap[budget.walletId]!!
        wallet_name_txt.text = wallet.name
        wallet_icon.setImageResource(wallet.imageId)

        range_title_txt.text = budget.rangeDetail

        startdate_txt.text = dateToString(toLocalDate(budget.startDate))
        enddate_txt.text = dateToString(toLocalDate(budget.endDate))

        setProgressProgressBar(progressBar2, (budget.spent.toFloat() / budget.amount * 100).toInt())
    }

    private fun drawLimitLine(limit: Long) {
        val limitLine = LimitLine(limit.toFloat())
        limitLine.lineWidth = 1f
        budget_chart.axisLeft.addLimitLine(limitLine)
    }

    private fun drawChart(results: Pair<List<Entry>, List<Entry>>) {
        var limit = max(viewModel.currentBudget.value!!.amount.toFloat(), viewModel.projectedSpending.value!!)
        limit = max(limit, viewModel.currentBudget.value!!.spent.toFloat())
        budget_chart.axisLeft.axisMaximum = limit + limit / 10

        val set1 = LineDataSet(results.first, "")
        set1.setDrawValues(false)
        set1.setDrawCircles(false)
        set1.lineWidth = 2f
        set1.setDrawFilled(true)
        set1.color = Color.rgb(4, 194, 111)
        set1.fillColor = Color.rgb(14, 235, 139)

        val set2 = LineDataSet(results.second, "")
        set2.setDrawValues(false)
        set2.setDrawCircles(false)
        set2.lineWidth = 2f
        set2.setDrawFilled(true)
        set2.color = Color.rgb(4, 194, 111)
        set2.fillColor = Color.rgb(184, 186, 185)
        set2.enableDashedLine(5f, 5f, 0f)

        val lineData = LineData(set1, set2)
        budget_chart.data = lineData
        budget_chart.invalidate()
    }

    private fun setViewTransactionsListBtn(budget: Budget)
    {
        view_transactions_list_btn.setOnClickListener {
            val transactionListIntent =
                Intent(this@BudgetDetailActivity, TransactionListActivity::class.java)

            transactionListIntent.putExtra(START_TIME_PARAM, budget.startDate)
            transactionListIntent.putExtra(END_TIME_PARAM, budget.endDate)
            transactionListIntent.putExtra(WALLET_ID_PARAM, budget.walletId)
            transactionListIntent.putExtra(TIME_RANGE_PARAM, TimeRange.MONTH.value)

            var filteringParams = FilteringParams(budget.categoryId,Constants.TYPE_EXPENSE,false)
            transactionListIntent.putExtra(RANGE_PARAMS, filteringParams)
            startActivity(transactionListIntent)
        }
    }
}