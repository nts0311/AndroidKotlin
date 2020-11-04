package com.android.walletforest.main_activity

import com.android.walletforest.enums.TimeRange
import com.android.walletforest.utils.toEpoch
import com.android.walletforest.utils.toLocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TabInfoUtils {
    companion object {
        fun getTabInfoList(
            start: Long,
            end: Long,
            timeRange: TimeRange,
            walletId: Long
        ): Flow<List<TabInfo>> {
            return flow{
                val dStart = toLocalDate(start)
                val dEnd = toLocalDate(end)

                emit(when (timeRange) {
                    TimeRange.WEEK -> getTabInfoByWeek(dStart, dEnd, walletId)
                    TimeRange.MONTH -> getTabInfoByMonth(dStart, dEnd, walletId)
                    TimeRange.YEAR -> getTabInfoByYear(dStart, dEnd, walletId)
                    TimeRange.CUSTOM -> getTabInfoCustomRange(dStart, dEnd, walletId)
                })
            }.flowOn(Dispatchers.Default)
        }

        private fun getWeekTitle(ld1: LocalDate, ld2: LocalDate): String {
            val sb = StringBuilder("")
            sb.append(ld1.dayOfMonth.toString() + "/" + ld1.month.value)
            sb.append(" - ")
            sb.append(ld2.dayOfMonth.toString() + "/" + ld2.month.value)
            return sb.toString()
        }

        private fun funAddFutureTab(
            result: MutableList<TabInfo>,
            currentTimeTitle: String,
            startTime: Long,
            walletId: Long
        ) {
            result[result.size - 1].tabTitle = currentTimeTitle
            result.add(TabInfo(walletId, startTime, Long.MAX_VALUE, "Future"))
        }

        private fun getTabInfoByWeek(
            start: LocalDate, end: LocalDate,
            walletId: Long
        ): List<TabInfo> {
            val result = mutableListOf<TabInfo>()
            var dStart = start
            var dEnd = end

            if (dStart.dayOfWeek <= DayOfWeek.SUNDAY)
                dStart = dStart.minusDays(dStart.dayOfWeek.value.toLong() - 1)

            if (dEnd.dayOfWeek <= DayOfWeek.SUNDAY)
                dEnd = dEnd.plusDays((7 - dEnd.dayOfWeek.value).toLong())

            val weekDiff = ChronoUnit.WEEKS.between(dStart, dEnd)

            for (i in 0..weekDiff.toInt()) {
                val nextWeek = dStart.plusDays(6)

                result.add(
                    TabInfo(
                        walletId,
                        toEpoch(dStart),
                        toEpoch(nextWeek),
                        getWeekTitle(dStart, nextWeek)
                    )
                )

                dStart = nextWeek.plusDays(1)
            }

            funAddFutureTab(result, "This week", toEpoch(dStart), walletId)
            return result
        }

        private fun getMonthTitle(ld: LocalDate): String =
            ld.monthValue.toString() + "/" + ld.year.toString()

        private fun getTabInfoByMonth(
            start: LocalDate, end: LocalDate,
            walletId: Long
        ): List<TabInfo> {
            val result = mutableListOf<TabInfo>()
            var dStart = LocalDate.of(start.year, start.monthValue, 1)
            val dEnd = LocalDate.of(end.year, end.monthValue + 1, 1).minusDays(1)

            val monthDiff = ChronoUnit.MONTHS.between(dStart, dEnd)

            for (i in 0..monthDiff.toInt()) {
                val nextMonth = dStart.plusMonths(1)
                result.add(
                    TabInfo(
                        walletId,
                        toEpoch(dStart),
                        //-1 to fix the error when adding the transaction on
                        // the first day of month it show on 2 different tabs
                        toEpoch(nextMonth) - 1,
                        getMonthTitle(dStart)
                    )
                )
                dStart = nextMonth
            }

            funAddFutureTab(result, "This month", toEpoch(dStart), walletId)

            return result
        }

        private fun getTabInfoByYear(
            start: LocalDate, end: LocalDate,
            walletId: Long
        ): List<TabInfo> {
            val result = mutableListOf<TabInfo>()
            var dStart = LocalDate.of(start.year, 1, 1)
            val dEnd = LocalDate.of(end.year + 1, 1, 1).minusDays(1)

            val yearDiff = ChronoUnit.YEARS.between(dStart, dEnd)

            for (i in 0..yearDiff.toInt()) {
                val nextYear = dStart.plusYears(1)
                result.add(
                    TabInfo(
                        walletId,
                        toEpoch(dStart),
                        toEpoch(nextYear) - 1,
                        dStart.year.toString()
                    )
                )
                dStart = nextYear
            }

            funAddFutureTab(result, "This year", toEpoch(dStart), walletId)
            return result
        }

        private fun getTabInfoCustomRange(
            start: LocalDate, end: LocalDate,
            walletId: Long
        ): List<TabInfo> {

            val result = mutableListOf<TabInfo>()
            val sb = java.lang.StringBuilder("")

            if (start.year == end.year) {
                sb.append(start.dayOfMonth.toString() + "/" + start.monthValue.toString())
                sb.append(" - ")
                sb.append(end.dayOfMonth.toString() + "/" + end.monthValue.toString())
            } else {
                sb.append(start.dayOfMonth.toString() + "/" + start.monthValue.toString() + "/" + start.year)
                sb.append(" - ")
                sb.append(end.dayOfMonth.toString() + "/" + end.monthValue.toString() + "/" + end.year)
            }

            result.add(
                TabInfo(
                    walletId,
                    toEpoch(start),
                    toEpoch(end),
                    sb.toString()
                )
            )
            return result
        }
    }
}
