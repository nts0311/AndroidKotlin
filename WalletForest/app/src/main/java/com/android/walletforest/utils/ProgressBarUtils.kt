package com.android.walletforest.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ProgressBar

fun setProgressProgressBar(progressBar: ProgressBar, progress: Int) {
    progressBar.progress = progress
    progressBar.scaleY = 1.5f
    progressBar.progressTintList = when (progress) {
        in 0..69 -> ColorStateList.valueOf(Color.rgb(0, 153, 51))
        in 70..89 -> ColorStateList.valueOf(Color.rgb(255, 153, 51))
        else -> ColorStateList.valueOf(Color.RED)
    }
}