package com.android.walletforest.model

import android.content.Context
import com.android.walletforest.enums.Constants
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.toEpoch
import com.android.walletforest.toEpochMilli
import com.android.walletforest.toLocalDate
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield


class ChartEntryGenerator() {

    companion object
    {
        suspend fun getBarEntries(
            transactions: List<Transaction>,
            start: Long,
            end: Long,
            timeRange: TimeRange
        ): List<BarEntry> {
            var rangeEndDate = end

            when (timeRange) {
                TimeRange.MONTH -> {
                    return getBarEntries(transactions, start, toEpoch(toLocalDate(start).plusDays(6)))
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
                    return getBarEntries(
                        transactions,
                        start,
                        toLocalDate(start).plusDays(1).toEpochMilli() - 1
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
                    return getBarEntries(
                        transactions,
                        start,
                        toLocalDate(start).plusMonths(1).minusDays(1).toEpochMilli()
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

                TimeRange.CUSTOM -> return getBarEntries(transactions, start, end) { start, end ->
                    Pair(start, end)
                }

                else -> return listOf()
            }
        }


        private suspend fun getBarEntries(
            transactions: List<Transaction>,
            start: Long,
            end: Long,
            getNextRange: (Long, Long) -> Pair<Long, Long>
        ): List<BarEntry> {
            return withContext(Dispatchers.Default)
            {
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

                result
            }
        }
    }
}

