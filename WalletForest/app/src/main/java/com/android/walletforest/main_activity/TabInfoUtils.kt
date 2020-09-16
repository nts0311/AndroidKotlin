package com.android.walletforest.main_activity

import com.android.walletforest.enums.TimeRange
import com.android.walletforest.main_activity.TabInfo
import com.android.walletforest.toEpoch
import com.android.walletforest.toLocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TabInfoUtils() {

    var start: Long = 0L
    var end: Long = 0L
    var timeRange: TimeRange = TimeRange.MONTH
    var walletId: Long = 0

    fun setProperties(start: Long, end: Long, timeRange: TimeRange, walletId: Long) {
        this.start = start
        this.end = end
        this.timeRange = timeRange
        this.walletId = walletId
    }

    private var _tabInfoList: MutableList<TabInfo> = mutableListOf()

    suspend fun getTabInfoList(): List<TabInfo> {
        _tabInfoList.clear()

        val dStart = toLocalDate(start)
        val dEnd = toLocalDate(end)

        when (timeRange) {
            TimeRange.WEEK -> getTabInfoByWeek(dStart, dEnd)
            TimeRange.MONTH -> getTabInfoByMonth(dStart, dEnd)
            TimeRange.YEAR -> getTabInfoByYear(dStart, dEnd)
            TimeRange.CUSTOM -> getTabInfoCustomRange(dStart, dEnd)
        }

        return _tabInfoList
    }

    private fun getWeekTitle(ld1: LocalDate, ld2: LocalDate): String {
        val sb = StringBuilder("")
        sb.append(ld1.dayOfMonth.toString() + "/" + ld1.month.value)
        sb.append(" - ")
        sb.append(ld2.dayOfMonth.toString() + "/" + ld2.month.value)
        return sb.toString()
    }

    private fun funAddFutureTab(currentTimeTitle: String, startTime: Long) {
        _tabInfoList[_tabInfoList.size - 1].tabTitle = currentTimeTitle
        _tabInfoList.add(TabInfo(walletId, startTime, Long.MAX_VALUE, "Future"))
    }

    private suspend fun getTabInfoByWeek(start: LocalDate, end: LocalDate) {
        withContext(Dispatchers.Default)
        {
            var dStart = start
            var dEnd = end

            if (dStart.dayOfWeek <= DayOfWeek.SUNDAY)
                dStart = dStart.minusDays(dStart.dayOfWeek.value.toLong() - 1)

            if (dEnd.dayOfWeek <= DayOfWeek.SUNDAY)
                dEnd = dEnd.plusDays((7 - dEnd.dayOfWeek.value).toLong())

            val weekDiff = ChronoUnit.WEEKS.between(dStart, dEnd)

            for (i in 0..weekDiff.toInt()) {
                val nextWeek = dStart.plusDays(6)

                _tabInfoList.add(
                    TabInfo(
                        walletId,
                        toEpoch(dStart),
                        toEpoch(nextWeek),
                        getWeekTitle(dStart, nextWeek)
                    )
                )

                dStart = nextWeek.plusDays(1)
            }

            funAddFutureTab("This week", toEpoch(dStart))
        }
    }

    private fun getMonthTitle(ld: LocalDate): String =
        ld.monthValue.toString() + "/" + ld.year.toString()

    private suspend fun getTabInfoByMonth(start: LocalDate, end: LocalDate) {
        withContext(Dispatchers.Default)
        {
            var dStart = LocalDate.of(start.year, start.monthValue, 1)
            val dEnd = LocalDate.of(end.year, end.monthValue + 1, 1).minusDays(1)

            val monthDiff = ChronoUnit.MONTHS.between(dStart, dEnd)

            for (i in 0..monthDiff.toInt()) {
                val nextMonth = dStart.plusMonths(1)
                _tabInfoList.add(
                    TabInfo(
                        walletId,
                        toEpoch(dStart),
                        toEpoch(nextMonth),
                        getMonthTitle(dStart)
                    )
                )
                dStart = nextMonth
            }

            funAddFutureTab("This month", toEpoch(dStart))
        }
    }

    private suspend fun getTabInfoByYear(start: LocalDate, end: LocalDate) {
        withContext(Dispatchers.Default)
        {
            var dStart = LocalDate.of(start.year, 1, 1)
            val dEnd = LocalDate.of(end.year + 1, 1, 1).minusDays(1)

            val yearDiff = ChronoUnit.YEARS.between(dStart, dEnd)

            for (i in 0..yearDiff.toInt()) {
                val nextYear = dStart.plusYears(1)
                _tabInfoList.add(
                    TabInfo(
                        walletId,
                        toEpoch(dStart),
                        toEpoch(nextYear),
                        dStart.year.toString()
                    )
                )
                dStart = nextYear
            }

            funAddFutureTab("This year", toEpoch(dStart))
        }
    }

    private suspend fun getTabInfoCustomRange(start: LocalDate, end: LocalDate) {
        withContext(Dispatchers.Default)
        {

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

            _tabInfoList.add(
                TabInfo(
                    walletId,
                    toEpoch(start),
                    toEpoch(end),
                    sb.toString()
                )
            )
        }
    }
}