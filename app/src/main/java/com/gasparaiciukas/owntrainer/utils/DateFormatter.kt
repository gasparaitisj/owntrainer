package com.gasparaiciukas.owntrainer.utils

class DateFormatter {
    companion object {
        fun dayOfWeekToString(dayOfWeek: Int): String {
            return when (dayOfWeek) {
                1 -> "Mon"
                2 -> "Tue"
                3 -> "Wed"
                4 -> "Thu"
                5 -> "Fri"
                6 -> "Sat"
                7 -> "Sun"
                else -> ""
            }
        }

        fun monthOfYearToString(monthOfYear: Int): String {
            return when (monthOfYear) {
                1 -> " January "
                2 -> " February "
                3 -> " March "
                4 -> " April "
                5 -> " May "
                6 -> " June "
                7 -> " July "
                8 -> " August "
                9 -> " September "
                10 -> " October "
                11 -> " November "
                12 -> " December "
                else -> ""
            }
        }
    }
}