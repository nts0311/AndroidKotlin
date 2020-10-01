package com.android.walletforest.report_record_fragment

import com.android.walletforest.*
import com.android.walletforest.enums.Constants
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Entities.Transaction
import kotlinx.coroutines.yield


class ChartEntryGenerator() {

    companion object {
        suspend fun getBarChartData(
            transactions: List<Transaction>,
            start: Long,
            end: Long,
            timeRange: TimeRange
        ): List<BarChartData> {

            if(transactions.isEmpty()) return listOf()

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

                TimeRange.CUSTOM -> return getBarData(
                    transactions,
                    start,
                    end,
                    timeRange
                ) { start, end ->
                    Pair(start, end)
                }

                else -> return listOf()
            }
        }


        private suspend fun getBarChartData(
            transactions: List<Transaction>,
            start: Long,
            end: Long,
            timeRange: TimeRange,
            getNextRange: (Long, Long) -> Pair<Long, Long>
        ): MutableList<BarChartData> {
            val result = mutableListOf<BarChartData>()
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

                    val label = when (timeRange) {
                        TimeRange.MONTH -> getMonthAxisLabel(startTime, endTime)
                        TimeRange.WEEK -> getWeekAxisLabel(startTime)
                        TimeRange.YEAR -> getYearAxisLabel(startTime)
                        TimeRange.CUSTOM -> getCustomAxisLabel(startTime, endTime)
                    }

                    result.add(BarChartData(label, totalIncome, totalExpense, startTime, endTime))

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
        ): List<BarChartData> {

            val maxEntryCount = when (timeRange) {
                TimeRange.MONTH -> 5
                TimeRange.WEEK -> 7
                TimeRange.YEAR -> 12
                TimeRange.CUSTOM -> 1
            }

            var startTime = start
            var endTime = end

            val result = getBarChartData(transactions, startTime, endTime, timeRange,getNextRange)
            startTime = result.last().startDate
            endTime = result.last().endDate

            //Fill result with empty data

            while (result.size != maxEntryCount) {
                yield()
                val label = when (timeRange) {
                    TimeRange.MONTH -> getMonthAxisLabel(startTime, endTime)
                    TimeRange.WEEK -> getWeekAxisLabel(startTime)
                    TimeRange.YEAR -> getYearAxisLabel(startTime)
                    TimeRange.CUSTOM -> getCustomAxisLabel(startTime, endTime)
                }

                result.add(BarChartData(label, 0, 0, startTime, endTime))

                val nextRange = getNextRange(start, end)
                startTime = nextRange.first
                endTime = nextRange.second
            }
            return result
        }
    }
}

