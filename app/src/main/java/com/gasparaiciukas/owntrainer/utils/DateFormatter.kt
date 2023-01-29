package com.gasparaiciukas.owntrainer.utils

import android.content.Context
import com.gasparaiciukas.owntrainer.R

class DateFormatter {
    companion object {
        fun dayOfWeekToString(dayOfWeek: Int, context: Context): String {
            return when (dayOfWeek) {
                1 -> context.getString(R.string.monday_three_letter)
                2 -> context.getString(R.string.tuesday_three_letter)
                3 -> context.getString(R.string.wednesday_three_letter)
                4 -> context.getString(R.string.thursday_three_letter)
                5 -> context.getString(R.string.friday_three_letter)
                6 -> context.getString(R.string.saturday_three_letter)
                7 -> context.getString(R.string.sunday_three_letter)
                else -> ""
            }
        }

        fun monthOfYearToString(monthOfYear: Int, context: Context): String {
            return when (monthOfYear) {
                1 -> context.getString(R.string.january)
                2 -> context.getString(R.string.february)
                3 -> context.getString(R.string.march)
                4 -> context.getString(R.string.april)
                5 -> context.getString(R.string.may)
                6 -> context.getString(R.string.june)
                7 -> context.getString(R.string.july)
                8 -> context.getString(R.string.august)
                9 -> context.getString(R.string.september)
                10 -> context.getString(R.string.october)
                11 -> context.getString(R.string.november)
                12 -> context.getString(R.string.december)
                else -> ""
            }
        }
    }
}
