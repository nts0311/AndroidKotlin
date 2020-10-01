package com.android.walletforest

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun dateToString(ld: LocalDate): String =
    DateTimeFormatter.ofPattern("dd/MM/yyyy").format(ld)

fun toLocalDate(time: Long): LocalDate {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time

    return LocalDate.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

fun LocalDate.toEpochMilli() : Long = this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun toEpoch(ld: LocalDate): Long =
    ld.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun getMonthAxisLabel(start: Long, end:Long) : String
{
    val startLD = toLocalDate(start)
    val endLD = toLocalDate(end)

    return "${startLD.dayOfMonth}/${startLD.monthValue} - ${endLD.dayOfMonth}/${endLD.monthValue}"
}

fun getWeekAxisLabel(time:Long) : String
{
    val ld = toLocalDate(time)
    return dateToString(ld)
}

fun getYearAxisLabel(time:Long) : String
{
    val ld = toLocalDate(time)
    return "${ld.monthValue}-${ld.year}"
}

fun getCustomAxisLabel(start: Long, end:Long) : String
{
    val startLD = toLocalDate(start)
    val endLD = toLocalDate(end)

    return "${startLD.dayOfMonth}/${startLD.month}/${startLD.year} - ${endLD.dayOfMonth}/${endLD.month}/${endLD.year}"
}