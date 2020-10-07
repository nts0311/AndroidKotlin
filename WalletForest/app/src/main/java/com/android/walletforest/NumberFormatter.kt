package com.android.walletforest

import android.icu.text.NumberFormat
import java.util.*

class NumberFormatter {
    companion object
    {
        private val nf = NumberFormat.getInstance(Locale.getDefault())
        fun format(number : Long) : String = nf.format(number)
    }
}