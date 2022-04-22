package com.gasparaiciukas.owntrainer.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.gasparaiciukas.owntrainer.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DateFormatterTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun incorrectDayOfWeek_returnsEmptyString() {
        val result = DateFormatter.dayOfWeekToString(13, context)
        assertThat(result).isEqualTo("")
    }

    @Test
    fun correctDayOfWeek_returnsCorrectString() {
        val result = DateFormatter.dayOfWeekToString(7, context)
        assertThat(result).isEqualTo(context.getString(R.string.sunday_three_letter))
    }

    @Test
    fun incorrectMonthOfYear_returnsEmptyString() {
        val result = DateFormatter.monthOfYearToString(13, context)
        assertThat(result).isEqualTo("")
    }

    @Test
    fun correctMonthOfYear_returnsCorrectString() {
        val result = DateFormatter.monthOfYearToString(7, context)
        assertThat(result).isEqualTo(context.getString(R.string.july))
    }
}