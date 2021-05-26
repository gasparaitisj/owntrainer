package com.gasparaiciukas.owntrainer.utils

import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class NutrientValueFormatter : ValueFormatter() {
    override fun getPieLabel(value: Float, pieEntry: PieEntry): String {
        pieEntry.label = "" // remove labels
        return super.getPieLabel(value, pieEntry)
    }

    override fun getFormattedValue(value: Float): String {
        val df = DecimalFormat("#")
        val floatToPercent = df.format(value.toDouble())
        return if (value == 0f) "" else "$floatToPercent%"
    }
}