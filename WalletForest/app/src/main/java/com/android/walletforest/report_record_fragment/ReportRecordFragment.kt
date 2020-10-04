package com.android.walletforest.report_record_fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ScaleDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.walletforest.R
import com.android.walletforest.RepoViewModelFactory
import com.android.walletforest.bar_chart_detail_activity.BarChartDetailActivity
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Repository
import com.android.walletforest.pie_chart_detail_activity.PieChartDetailActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.fragment_report_record.*
import java.io.Serializable
import java.util.ArrayList
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

        ChartsDrawer.setUpBarChart(income_expense_chart)

        registerObservers()
        registerClickListeners()

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
            ChartsDrawer.drawBarChartChart(income_expense_chart, it)
            displayIncomeExpenseInfo(it)
        }
    }

    private fun observePieChartData() {
        viewModel.pieChartData.removeObservers(viewLifecycleOwner)
        viewModel.pieChartData.observe(viewLifecycleOwner) {
            drawPieCharts(it)
        }
    }

    private fun registerClickListeners() {
        income_expense_chart.setOnClickListener {
            val intent = Intent(requireContext(), BarChartDetailActivity::class.java)
            intent.putExtra(
                BarChartDetailActivity.BAR_DATA_KEY,
                viewModel.barData.value as Serializable
            )
            intent.putExtra(BarChartDetailActivity.WALLET_ID_KEY, walletId)
            startActivity(intent)
        }

        expense_chart.onChartGestureListener = object : OnChartGestureListener
        {
            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
            }

            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
            }

            override fun onChartLongPressed(me: MotionEvent?) {
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }

            override fun onChartSingleTapped(me: MotionEvent?) {
                val intent = Intent(requireContext(), PieChartDetailActivity::class.java)
                intent.putExtra(
                    PieChartDetailActivity.PIE_DATA_KEY,
                    viewModel.pieChartData.value!! as Serializable
                )
                intent.putExtra(
                    PieChartDetailActivity.IS_EXPENSE_KEY,
                    true
                )
                intent.putExtra(BarChartDetailActivity.WALLET_ID_KEY, walletId)
                startActivity(intent)
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
            }
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

    private fun drawPieCharts(pieChartData: PieChartData) {
        if (pieChartData.incomePieEntries.isNotEmpty())
            ChartsDrawer.drawPieChart(income_chart, pieChartData.incomePieEntries)
        if (pieChartData.expensePieEntries.isNotEmpty())
            ChartsDrawer.drawPieChart(expense_chart, pieChartData.expensePieEntries)
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