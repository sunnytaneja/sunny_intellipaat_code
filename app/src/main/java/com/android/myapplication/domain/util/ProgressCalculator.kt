package com.android.myapplication.domain.util

import kotlin.math.roundToInt

object ProgressCalculator {

    /** Returns completed/total as a whole percentage, always within 0..100. */
    fun percent(completed: Int, total: Int): Int {
        if (total <= 0) return 0
        val safeCompleted = completed.coerceIn(0, total)
        return (safeCompleted * 100f / total).roundToInt()
    }
}
