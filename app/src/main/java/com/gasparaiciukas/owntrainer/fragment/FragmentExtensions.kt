package com.gasparaiciukas.owntrainer.fragment

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.utils.NutrientValueFormatter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlin.math.roundToInt

fun Fragment.setPieChart(
    carbsPercentage: Float,
    fatPercentage: Float,
    proteinPercentage: Float,
    calories: Float,
    pieChart: PieChart,
    context: Context,
) {
    val colors = listOf(
        getColor(context, R.color.colorGold),   // carbs
        getColor(context, R.color.colorOrange), // fat
        getColor(context, R.color.colorSmoke)   // protein
    )

    val entries = listOf(
        PieEntry(carbsPercentage, getString(R.string.carbohydrates)),
        PieEntry(fatPercentage, getString(R.string.fat)),
        PieEntry(proteinPercentage, getString(R.string.protein))
    )

    val pieDataSet = PieDataSet(entries, "Data").apply {
        this.colors = colors
    }

    val pieData = PieData(pieDataSet).apply {
        setValueFormatter(NutrientValueFormatter())
        setValueTextSize(12f)
    }

    pieChart.apply {
        data = pieData
        centerText = "${calories.roundToInt()}\nkCal"
        setCenterTextSize(14f)
        setCenterTextColor(
            getColor(context, R.color.colorWhite)
        )
        centerTextRadiusPercent = 100f
        setHoleColor(getColor(context, R.color.colorRed))
        holeRadius = 30f
        transparentCircleRadius = 0f
        legend.isEnabled = false
        description.isEnabled = false
        setTouchEnabled(false)
        invalidate()
    }
}