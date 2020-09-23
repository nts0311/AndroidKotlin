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