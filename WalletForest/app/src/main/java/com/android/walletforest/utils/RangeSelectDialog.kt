package com.android.walletforest.utils

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.android.walletforest.R
import java.time.LocalDate

fun createRangeSelectDialog(context : Context, positiveBtnClickListener : (Long, Long) -> Unit) : AlertDialog {
    return AlertDialog.Builder(context).run {

        val inflater = LayoutInflater.from(context)

        val dialogView = inflater.inflate(R.layout.range_selection_dialog, null)

        val startEdt = dialogView.findViewById<EditText>(R.id.start_time_edt)
        val endEdt = dialogView.findViewById<EditText>(R.id.end_time_edt)

        setView(dialogView)

        setPositiveButton(R.string.select_time) { _, _ ->
            //viewModel.onSelectCustomTimeRange(startEdt.tag as Long, endEdt.tag as Long)
            positiveBtnClickListener.invoke(startEdt.tag as Long, endEdt.tag as Long)
        }

        setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        create()
    }
}

fun showRangeSelectDialog(context: Context, rangeSelectDialog : AlertDialog?, startTime: Long, endTime : Long) {

    rangeSelectDialog?.show()

    val startDate = toLocalDate(startTime)
    val endDate = toLocalDate(endTime)

    val startEdt = rangeSelectDialog?.findViewById<EditText>(R.id.start_time_edt)
    startEdt?.setText(dateToString(startDate))
    startEdt?.tag = toEpoch(startDate)

    val endEdt = rangeSelectDialog?.findViewById<EditText>(R.id.end_time_edt)
    endEdt?.setText(dateToString(endDate))
    endEdt?.tag = toEpoch(endDate)

    val startDateSetListener: (DatePicker, Int, Int, Int) -> Unit =
        { _, year, monthOfYear, dayOfMonth ->
            val ld = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            startEdt?.setText(dateToString(ld))
            startEdt?.tag = toEpoch(ld)
        }

    val endDateSetListener: (DatePicker, Int, Int, Int) -> Unit =
        { _, year, monthOfYear, dayOfMonth ->
            val ld = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            endEdt?.setText(dateToString(ld))
            endEdt?.tag = toEpoch(ld)
        }

    startEdt?.setOnClickListener {
        DatePickerDialog(
            context, startDateSetListener,
            startDate.year, startDate.monthValue - 1, startDate.dayOfMonth
        ).show()
    }

    endEdt?.setOnClickListener {
        DatePickerDialog(
            context, endDateSetListener,
            endDate.year, endDate.monthValue - 1, endDate.dayOfMonth
        ).show()
    }
}