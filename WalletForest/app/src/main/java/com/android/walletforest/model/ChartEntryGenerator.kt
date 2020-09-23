package com.android.walletforest.model

import android.content.Context
import com.android.walletforest.enums.Constants
import com.android.walletforest.enums.TimeRange
import com.android.walletforest.model.Entities.Transaction
import com.android.walletforest.toEpoch
import com.android.walletforest.toLocalDate
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield


class ChartEntryGenerator(appContext: Context) {

    suspend fun getBarEntries(
        transactions: List<Transaction>,
        start: Long,
        end: Long,
        timeRange: TimeRange
    ): List<BarEntry> {
        var rangeStartDate = start
        var rangeEndDate = end

        when (timeRange) {
            TimeRange.MONTH -> {
                return getBarEntries(transactions, start, toEpoch(toLocalDate(start).plusDays(6)))
                { _, end ->
                    val endDate = toLocalDate(end)
                    val nextEndDate = endDate.plusDays(6)

                    val nextEndDateL = toEpoch(nextEndDate)

                    if (nextEndDateL <= rangeEndDate)
                        Pair(end, nextEndDateL)
                    else {
                        val endOfMonth = nextEndDate.minusDays(nextEndDate.dayOfMonth.toLong())
                        Pair(end, toEpoch(endOfMonth))
                    }
                }
            }
            else -> return listOf<BarEntry>()
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

