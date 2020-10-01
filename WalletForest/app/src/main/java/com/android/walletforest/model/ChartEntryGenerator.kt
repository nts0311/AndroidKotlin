package com.android.walletforest.model

import android.graphics.Color
import com.android.walletforest.*
import com.android.walletforest.enums.Constants
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Entities.Transaction
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.yield


class ChartEntryGenerator() {

    companion object {
        suspend fun getBarEntries(
            transactions: List<Transaction>,
            start: Long,
            end: Long,
            timeRange: TimeRange
        ): BarData {
            var rangeEndDate = end

            when (timeRange) {
                TimeRange.MONTH -> {
                    return getBarData(
                        transactions,
                        start,
                        toEpoch(toLocalDate(start).plusDays(6)),
                        timeRange
                    )
                    { _, end ->
                        val endDate = toLocalDate(end)
                        val nextStartDate = endDate.plusDays(1)
                        val nextEndDate = endDate.plusDays(7)

                        val nextEndDateL = toEpoch(nextEndDate)

                        if (nextEndDateL <= rangeEndDate)
                            Pair(toEpoch(nextStartDate), nextEndDateL)
                        else {
                            val endOfMonth = nextEndDate.minusDays(nextEndDate.dayOfMonth.toLong())
                            Pair(toEpoch(nextStartDate), toEpoch(endOfMonth))
                        }
                    }
                }

                TimeRange.WEEK -> {
                    return getBarData(
                        transactions,
                        start,
                        toLocalDate(start).plusDays(1).toEpochMilli() - 1,
                        timeRange
                    ) { _, previousEnd ->

                        val nextStartDate = toLocalDate(previousEnd).plusDays(1L).toEpochMilli()
                        val nextEndDate = toLocalDate(previousEnd).plusDays(2L).toEpochMilli() - 1

                        if (nextEndDate <= rangeEndDate)
                            Pair(nextStartDate, nextEndDate)
                        else
                            Pair(nextStartDate, rangeEndDate)
                    }
                }

                TimeRange.YEAR -> {
                    return getBarData(
                        transactions,
                        start,
                        toLocalDate(start).plusMonths(1).minusDays(1).toEpochMilli(),
                        timeRange
                    )
                    { _, previousEnd ->
                        val nextStartDate = toLocalDate(previousEnd).plusDays(1)
                        val nextEndDate = nextStartDate.plusMonths(1).minusDays(1)

                        if (nextEndDate.toEpochMilli() <= rangeEndDate)
                            Pair(nextStartDate.toEpochMilli(), nextEndDate.toEpochMilli())
                        else
                            Pair(nextStartDate.toEpochMilli(), rangeEndDate)
                    }
                }

                TimeRange.CUSTOM -> return getBarData(transactions, start, end, timeRange) { start, end ->
                    Pair(start, end)
                }

                else -> return BarData()
            }
        }


        private suspend fun getBarEntries(
            transactions: List<Transaction>,
            start: Long,
            end: Long,
            getNextRange: (Long, Long) -> Pair<Long, Long>
        ): MutableList<BarEntry> {
            val result = mutableListOf<BarEntry>()
            var startTime = start
            var endTime = end

            var totalIncome = 0L
            var totalExpense = 0L
            var xPos = 0f

            for (transaction in transactions) {
                yield()
                if (transaction.time in startTime..endTime) {
                    if (transaction.type == Constants.TYPE_INCOME)
                        totalIncome += transaction.amount
                    else
                        totalExpense -= transaction.amount
                } else {
                    result.add(BarEntry(xPos, totalIncome.toFloat()))
                    result.add(BarEntry(xPos, totalExpense.toFloat()))
                    xPos++

                    val nextRange = getNextRange(start, end)
                    startTime = nextRange.first
                    endTime = nextRange.second

                    if (transaction.type == Constants.TYPE_INCOME)
                        totalIncome = transaction.amount
                    else
                        totalExpense = -transaction.amount
                }
            }

            return result
        }

        private suspend fun getBarData(
            transactions: List<Transaction>,
            start: Long,
            end: Long,
            timeRange: TimeRange,
            getNextRange: (Long, Long) -> Pair<Long, Long>
        ): BarData {

            val maxEntryCount = when (timeRange) {
                TimeRange.MONTH -> 5
                TimeRange.WEEK -> 7
                TimeRange.YEAR -> 12
                TimeRange.CUSTOM -> 1
            }

            val barEntries = getBarEntries(transactions,start, end,getNextRange)

            var startTime = start
            var endTime = end

            val xAxisLabel = mutableListOf<String>()

            val green = Color.rgb(110, 190, 102)
            val red = Color.rgb(211, 74, 88)

            var count = barEntries.size
            while (barEntries.size != maxEntryCount*2) {
               yield()

                barEntries.add(BarEntry(count.toFloat(), 0f))
                barEntries.add(BarEntry(count.toFloat(), 0f))
                count++

                val label = when (timeRange) {
                    TimeRange.MONTH -> getMonthAxisLabel(startTime, endTime)
                    TimeRange.WEEK -> getWeekAxisLabel(startTime)
                    TimeRange.YEAR -> getYearAxisLabel(startTime)
                    TimeRange.CUSTOM -> getCustomAxisLabel(startTime, endTime)
                }

                xAxisLabel.add(label)

                val nextRange = getNextRange(start, end)
                startTime = nextRange.first
                endTime = nextRange.second
            }

            val set = BarDataSet(barEntries, "")
            set.colors = List(maxEntryCount*2) {
                if (it % 2 == 0) green
                else red
            }

            val data = BarData(set)
            data.barWidth=0.8f

            return data
        }

    }
}

