package com.android.walletforest.budget_detail_activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.add_budget_activity.AddBudgetActivity
import com.android.walletforest.model.repositories.Repository
import com.android.walletforest.utils.NumberFormatter
import com.android.walletforest.utils.setProgressProgressBar
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.xxmassdeveloper.mpchartexample.custom.com.android.walletforest.MyMarkerView
import kotlinx.android.synthetic.main.activity_budget_detail.*

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

            axisLeft.valueFormatter = LargeValueFormatter().apply {
                setSuffix(arrayOf("", "K", "M", "B", "T"))
            }

            // create marker to display box when values are selected
            val mv = MyMarkerView(this@BudgetDetailActivity, R.layout.custom_marker)
            mv.chartView = this
            marker = mv
        }
    }

    private fun drawChart()
    {

    }

    private fun registerObservers() {
        viewModel.currentBudget.observe(this)
        {
            if (it==null)
                return@observe

            viewModel.setBudgetInfo(it)

            if (it.categoryId==-1L) {
                category_img.setImageResource(R.drawable.ic_category_all)
                category_txt.text = applicationContext.getString(R.string.all_categories)
            } else {
                val category = viewModel.categoryMap[it.categoryId]!!
                category_img.setImageResource(category.imageId)
                category_txt.text = category.name
            }

            amount_txt.text = NumberFormatter.format(it.amount)
            amount_spent_txt.text = NumberFormatter.format(it.spent)
            amount_left_txt.text = NumberFormatter.format(it.amount - it.spent)

            val wallet = viewModel.walletMap[it.walletId]!!
            wallet_name_txt.text = wallet.name
            wallet_icon.setImageResource(wallet.imageId)

            range_title_txt.text = it.rangeDetail

            setProgressProgressBar(progressBar2, (it.spent.toFloat() / it.amount * 100).toInt())
        }

        viewModel.lineEntries.observe(this)
        {
            val limit = viewModel.currentBudget.value!!.amount.toFloat()
            budget_chart.axisLeft.axisMaximum = limit + limit/10

            val set1 = LineDataSet(it.first, "")
            set1.setDrawValues(false)
            set1.setDrawCircles(false)
            set1.lineWidth=2f

            val set2 = LineDataSet(it.second, "")
            set2.setDrawValues(false)
            set2.setDrawCircles(false)
            set2.lineWidth=2f

            val lineData = LineData(set1, set2)

            budget_chart.data = lineData

        }
    }
}