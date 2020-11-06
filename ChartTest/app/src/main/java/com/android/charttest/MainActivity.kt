package com.android.charttest

import android.graphics.Color
import android.graphics.drawable.ScaleDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    /*private fun setupBarChart() {
        barChart.xAxis.apply {
            setDrawGridLines(true)
        }

        barChart.legend.isEnabled = false
        barChart.axisRight.isEnabled = false

        barChart.xAxis.position = XAxisPosition.BOTTOM
        barChart.xAxis.setDrawGridLines(false)
        //barChart.xAxis.setDrawLabels(false)
        barChart.xAxis.labelCount = 4
        barChart.xAxis.labelRotationAngle = 45f
        barChart.description.text = ""


        val entries = mutableListOf<BarEntry>()


        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)
        val colors = listOf(green, red, red, green, green, red, green, red, green, red, green, red)

        entries.add(BarEntry(0f, 2f))
        entries.add(BarEntry(0f, 0f))
        entries.add(BarEntry(1f, -5f))
        entries.add(BarEntry(1f, 5f))
        entries.add(BarEntry(2f, 8f))
        entries.add(BarEntry(2f, -2.6f))
        entries.add(BarEntry(3f, 10f))
        entries.add(BarEntry(3f, -6f))
        entries.add(BarEntry(4f, 7f))
        entries.add(BarEntry(4f, -12f))
        entries.add(BarEntry(5f, 1f))
        entries.add(BarEntry(5f, -4f))

        val set = BarDataSet(entries, "")
        set.colors = colors
        val data = BarData(set)
        data.barWidth = 0.7f
        barChart.data = data
        barChart.invalidate()
    }


    private fun setUpChart() {
        val pieEntries = mutableListOf<PieEntry>()
        val ds = getDrawable(R.drawable.ic_category_friendnlover)

        val sd = ScaleDrawable(ds, Gravity.CENTER, 1f, 1f)
        sd.level = 5000
        sd.invalidateSelf()

        (0..5).forEach { i ->
            pieEntries.add(PieEntry(rainFall[i], sd))
        }
        val dataSet = PieDataSet(pieEntries, "Rain")

        dataSet.colors = ColorTemplate.COLORFUL_COLORS.asList()

        dataSet.iconsOffset= MPPointF(0f, 25f)

        chart.setUsePercentValues(true)
        chart.setExtraOffsets(10f, 0f, 10f, 0f)
        val data = PieData(dataSet)
        chart.data = data
        chart.legend.isEnabled = false
        chart.animateY(700)
        chart.invalidate()
    }

    private fun IntArray.asList(): List<Int> {
        val list = mutableListOf<Int>()
        onEach { list.add(it) }
        return list
    }*/
}