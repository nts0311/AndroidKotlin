package com.android.walletforest.report_record_fragment

import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF

class ChartsDrawer {
    companion object{
        fun setUpBarChart(barChart : BarChart) {
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

        fun drawBarChartChart(barChart: BarChart, barDataList: List<BarChartData>) {

            if (barDataList.isEmpty()) return


            val barEntries = mutableListOf<BarEntry>()

            var xPos = 0
            barDataList.forEach {
                barEntries.add(BarEntry(xPos.toFloat(), it.totalIncome.toFloat()))
                barEntries.add(BarEntry(xPos.toFloat(), it.totalExpense.toFloat()))
                xPos++
            }

            barChart.xAxis.apply {
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
            barChart.data = data
            barChart.invalidate()
        }

        fun drawPieChart(pieChart: PieChart, pieEntries: List<PieEntry>) {
            pieChart.setExtraOffsets(0f, 10f, 0f, 15f)
            pieChart.description.isEnabled = false
            pieChart.transparentCircleRadius = 65f
            pieChart.setTransparentCircleColor(Color.rgb(36, 36, 36))
            pieChart.isRotationEnabled = false

            val dataSet = PieDataSet(pieEntries, "")
            dataSet.colors = listOf(
                Color.rgb(5, 64, 82), Color.rgb(24, 184, 130),
                Color.rgb(37, 168, 230), Color.rgb(242, 198, 7),
                Color.rgb(237, 91, 28), Color.rgb(224, 139, 90)
            )
            dataSet.iconsOffset = MPPointF(0f, 25f)

            dataSet.setDrawValues(false)

            pieChart.setUsePercentValues(true)

            val data = PieData(dataSet)
            pieChart.legend.isEnabled = false

            pieChart.data = data
            pieChart.invalidate()
        }
    }
}