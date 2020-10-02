package com.android.walletforest.report_record_fragment

import android.graphics.Color
import android.graphics.drawable.ScaleDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Repository
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.fragment_report_record.*

private const val START_TIME_PARAM = "startTime"
private const val END_TIME_PARAM = "endTime"
private const val WALLET_ID_PARAM = "walletId"
private const val TIME_RANGE_PARAM = "timeRange"

class ReportRecordFragment : Fragment() {
    private var startTime: Long? = null
    private var endTime: Long? = null
    private var walletId: Long? = null
    private var timeRange: String? = null
    var viewModelKey = ""

    private lateinit var viewModel: ReportRecordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            startTime = it.getLong(START_TIME_PARAM)
            endTime = it.getLong(END_TIME_PARAM)
            walletId = it.getLong(WALLET_ID_PARAM)
            timeRange = it.getString(TIME_RANGE_PARAM, TimeRange.MONTH.value)
            viewModelKey = "$startTime - $endTime"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val repo = Repository.getInstance(requireContext().applicationContext)
        val vmFactory = RepoViewModelFactory(repo)

        viewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        ).get(viewModelKey, ReportRecordViewModel::class.java)

        if (startTime != null && endTime != null && timeRange != null) {
            viewModel.setTimeRange(startTime!!, endTime!!, timeRange!!, walletId!!)
        }

        return inflater.inflate(R.layout.fragment_report_record, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpIncomeExpenseChart()

        registerObservers()
    }

    private fun registerObservers() {
        viewModel.currentWallet.observe(viewLifecycleOwner)
        {
            viewModel.setTimeRange(startTime!!, endTime!!, timeRange!!, it.id)
            observeChartData()
        }
    }

    private fun observeChartData() {
        viewModel.barData.observe(viewLifecycleOwner) {
            drawInComeExpenseChart(it.first)
            drawPieCharts(it.second)
        }
    }

    private fun setUpIncomeExpenseChart() {
        val barChart = income_expense_chart

        barChart.apply {
            xAxis.setDrawGridLines(true)
            legend.isEnabled = false
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)

            xAxis.labelRotationAngle = 45f
            xAxis.setDrawAxisLine(false)
            description.isEnabled = false

            axisLeft.valueFormatter = LargeValueFormatter().apply {
                setSuffix(arrayOf("", "K", "M", "B", "T"))
            }
        }
    }

    private fun drawInComeExpenseChart(barDataList: List<BarChartData>) {

        if (barDataList.isEmpty()) return

        val barEntries = mutableListOf<BarEntry>()

        var xPos = 0
        barDataList.forEach {
            barEntries.add(BarEntry(xPos.toFloat(), it.totalIncome.toFloat()))
            barEntries.add(BarEntry(xPos.toFloat(), it.totalExpense.toFloat()))
            xPos++
        }

        income_expense_chart.xAxis.apply {
            axisMaximum = barDataList.size.toFloat()
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (value >= barDataList.size) return ""

                    return barDataList[value.toInt()].xAxisLabel
                }
            }
            labelCount = barDataList.size
        }


        val set = BarDataSet(barEntries, "")
        val blue = Color.rgb(52, 134, 235)
        val red = Color.rgb(211, 74, 88)
        set.colors = List(barDataList.size * 2) { if (it % 2 == 0) blue else red }

        val data = BarData(set)
        data.barWidth = 0.8f

        income_expense_chart.data = data
        income_expense_chart.invalidate()
    }

    private fun drawPieCharts(pieChartData: PieChartData) {
        if (pieChartData.incomePieList.isNotEmpty())
            drawPieChart(income_chart, pieChartData.incomePieList)
        if (pieChartData.expensePieList.isNotEmpty())
            drawPieChart(expense_chart, pieChartData.expensePieList)
    }


    private fun drawPieChart(pieChart: PieChart, pieList: List<Pair<Long, Long>>) {
        pieChart.setExtraOffsets(0f, 10f, 0f, 15f)
        pieChart.description.isEnabled = false
        pieChart.transparentCircleRadius = 65f
        pieChart.setTransparentCircleColor(Color.rgb(36, 36, 36))
        pieChart.isRotationEnabled = false

        val dataSet = PieDataSet(getPieEntries(pieList), "")
        dataSet.colors = listOf(
            Color.rgb(5, 64, 82), Color.rgb(24, 184, 130),
            Color.rgb(31, 149, 204), Color.rgb(204, 167, 6), Color.rgb(214, 60, 9),
        )
        dataSet.iconsOffset = MPPointF(0f, 25f)

        dataSet.setDrawValues(false)

        pieChart.setUsePercentValues(true)

        val data = PieData(dataSet)
        pieChart.legend.isEnabled = false

        pieChart.data = data
        pieChart.invalidate()
    }

    private fun getDrawable(imageId: Int): ScaleDrawable {
        return ScaleDrawable(
            requireContext().getDrawable(imageId),
            Gravity.CENTER,
            1f,
            1f
        ).apply {
            level = 5000
            invalidateSelf()
        }
    }

    private fun getPieEntries(pieList: List<Pair<Long, Long>>): List<PieEntry> {

        val pieEntries = mutableListOf<PieEntry>()

        if (pieList.size <= 5) {
            pieList.forEach {
                val cateImage = getDrawable(viewModel.getCateImageId(it.first))
                pieEntries.add(PieEntry(it.second.toFloat(), cateImage))
            }
        } else {
            for (i in 0..4) {
                val cateImage = getDrawable(viewModel.getCateImageId(pieList[i].first))
                pieEntries.add(PieEntry(pieList[i].second.toFloat(), cateImage))
            }

            var otherCateAmount = 0L
            for (i in 5 until pieList.size)
                otherCateAmount += pieList[i].second

            pieEntries.add(
                PieEntry(
                    otherCateAmount.toFloat(),
                    getDrawable(R.drawable.ic_category_other_chart)
                )
            )
        }
        return pieEntries
    }

    companion object {
        @JvmStatic
        fun newInstance(startTime: Long, endTime: Long, walletId: Long, timeRange: TimeRange) =
            ReportRecordFragment().apply {
                arguments = Bundle().apply {
                    putLong(START_TIME_PARAM, startTime)
                    putLong(END_TIME_PARAM, endTime)
                    putLong(WALLET_ID_PARAM, walletId)
                    putString(TIME_RANGE_PARAM, timeRange.value)
                }
            }
    }
}