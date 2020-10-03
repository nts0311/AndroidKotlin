package com.android.walletforest.bar_chart_detail_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.android.walletforest.R
import com.android.walletforest.TransactionListFragment.TransactionListFragment
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.report_record_fragment.BarChartData
import com.android.walletforest.report_record_fragment.ChartsDrawer
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.activity_bar_chart_detail.*
import kotlin.math.absoluteValue

class BarChartDetailActivity : AppCompatActivity() {

    companion object {
        const val BAR_DATA_KEY = "bar_data_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart_detail)

        val barDataList = intent.getSerializableExtra(BAR_DATA_KEY) as List<BarChartData>
        ChartsDrawer.setUpBarChart(bar_chart)
        ChartsDrawer.drawBarChartChart(bar_chart, barDataList)

        addRangeData(barDataList)
    }

    private fun addRangeData(barDataList: List<BarChartData>) {
        val inflater = LayoutInflater.from(this)

        barDataList.forEach {
            val itemRoot = inflater.inflate(R.layout.item_bar_data, root_layout, false)

            itemRoot.findViewById<TextView>(R.id.range_label_txt).text = it.xAxisLabel
            itemRoot.findViewById<TextView>(R.id.income_txt).text = it.totalIncome.toString()
            itemRoot.findViewById<TextView>(R.id.expense_txt).text =
                it.totalExpense.absoluteValue.toString()
            itemRoot.findViewById<TextView>(R.id.net_income_txt).text =
                (it.totalIncome - it.totalExpense.absoluteValue).toString()

            itemRoot.setOnClickListener { view ->

                val transactionListFragment = TransactionListFragment.newInstance(
                    it.startDate,
                    it.endDate,
                    2,
                    TimeRange.MONTH)

                val tran = supportFragmentManager.beginTransaction()
                tran.replace(R.id.frag_container, transactionListFragment)
                tran.addToBackStack(null)
                tran.commit()
            }

            root_layout.addView(itemRoot)
        }
    }
}