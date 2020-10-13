package com.android.walletforest.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class AmountTextWatcher(private val editText: EditText) : TextWatcher {
    var beforeText = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString() != beforeText) {

            if (s.isNullOrEmpty()) {
                beforeText = ""
                editText.setText("")
            } else {
                val currentValue = NumberFormatter.toLong(s.toString())
                val formattedNumber = NumberFormatter.format(currentValue)
                beforeText = formattedNumber
                editText.setText(formattedNumber)

                if (formattedNumber.isNotEmpty())
                    editText.setSelection(formattedNumber.length)
                else
                    editText.setSelection(0)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }
}


