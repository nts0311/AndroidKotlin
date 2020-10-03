package com.android.walletforest.report_record_fragment

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Repository
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.fragment_report_record.*
import kotlin.math.absoluteValue

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
            viewModel.getPieEntries(startTime!!, endTime!!, walletId!!)
        }

        return inflater.inflate(R.layout.fragment_report_record, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpIncomeExpenseChart()

        registerObservers()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.report_frag_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.change_pie_mode) {
            viewModel.excludeSubCate = !viewModel.excludeSubCate
            viewModel.getPieEntries(startTime!!, endTime!!, walletId!!)
            observePieChartData()
        }

        return true
    }

    private fun registerObservers() {
        viewModel.currentWallet.observe(viewLifecycleOwner)
        {
            viewModel.setTimeRange(startTime!!, endTime!!, timeRange!!, it.id)
            viewModel.getPieEntries(startTime!!, endTime!!, walletId!!)
            observeBarChartData()
            observePieChartData()
        }
    }

    private fun observeBarChartData() {
        viewModel.barData.removeObservers(viewLifecycleOwner)
        viewModel.barData.observe(viewLifecycleOwner) {
            drawInComeExpenseChart(it)
            displayIncomeExpenseInfo(it)
        }
    }

    private fun observePieChartData() {
        viewModel.pieEntries.removeObservers(viewLifecycleOwner)
        viewModel.pieEntries.observe(viewLifecycleOwner) {
            drawPieCharts(it)
        }
    }

    private fun displayIncomeExpenseInfo(list: List<BarChartData>) {
        var income = 0L
        var expense = 0L

        list.forEach {
            income += it.totalIncome
            expense += it.totalExpense.absoluteValue
        }

        income_txt.text = income.toString()
        expense_txt.text = expense.toString()
        net_income_txt.text = (income - expense).toString()
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

    private fun drawPieCharts(pieEntriesPair: Pair<List<PieEntry>, List<PieEntry>>) {
        if (pieEntriesPair.first.isNotEmpty())
            drawPieChart(income_chart, pieEntriesPair.first)
        if (pieEntriesPair.second.isNotEmpty())
            drawPieChart(expense_chart, pieEntriesPair.second)
    }


    private fun drawPieChart(pieChart: PieChart, pieEntries: List<PieEntry>) {
        pieChart.setExtraOffsets(0f, 10f, 0f, 15f)
        pieChart.description.isEnabled = false
        pieChart.transparentCircleRadius = 65f
        pieChart.setTransparentCircleColor(Color.rgb(36, 36, 36))
        pieChart.isRotationEnabled = false

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.colors = listOf(
            Color.rgb(5, 64, 82), Color.rgb(24, 184, 130),
            Color.rgb(31, 149, 204), Color.rgb(204, 167, 6),
            Color.rgb(212, 81, 25), Color.rgb(224, 139, 90)
        )
        dataSet.iconsOffset = MPPointF(0f, 25f)

        dataSet.setDrawValues(false)

        pieChart.setUsePercentValues(true)

        val data = PieData(dataSet)
        pieChart.legend.isEnabled = false

        pieChart.data = data
        pieChart.invalidate()
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