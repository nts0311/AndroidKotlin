package com.android.walletforest.pie_chart_detail_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.walletforest.R
import com.android.walletforest.bar_chart_detail_activity.BarChartDetailActivity
import com.android.walletforest.report_record_fragment.BarChartData
import com.android.walletforest.report_record_fragment.ChartsDrawer
import com.android.walletforest.report_record_fragment.PieChartData
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.synthetic.main.activity_pie_chart_detail.*

class PieChartDetailActivity : AppCompatActivity() {

    companion object {
        const val IS_EXPENSE_KEY = "is_expense_key"
        const val WALLET_ID_KEY = "wallet_id"
    }

    private var walletId : Long = 1L
    private var isExpenseChart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_chart_detail)

        val pieChartData = PieChartDetailData.pieChartData
        isExpenseChart = intent.getBooleanExtra(IS_EXPENSE_KEY, true)

        walletId = intent.getLongExtra(WALLET_ID_KEY, 1L)

        if(isExpenseChart)
        {
            ChartsDrawer.drawPieChart(pie_chart, pieChartData.expensePieEntries, true)
        }
        else
        {
            ChartsDrawer.drawPieChart(pie_chart, pieChartData.incomePieEntries, true)
        }
    }

    private fun add
}