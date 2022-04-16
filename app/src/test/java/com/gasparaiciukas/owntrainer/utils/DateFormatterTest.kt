package com.gasparaiciukas.owntrainer.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DateFormatterTest {

    @Test
    fun `incorrect day of week returns empty string`() {
        val result = DateFormatter.dayOfWeekToString(13)
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `correct day of week returns correct string`() {
        val result = DateFormatter.dayOfWeekToString(7)
        assertThat(result).isEqualTo("Sun")
    }

    @Test
    fun `incorrect month of year returns empty string`() {
        val result = DateFormatter.monthOfYearToString(13)
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `correct month of year returns correct string`() {
        val result = DateFormatter.monthOfYearToString(7)
        assertThat(result).isEqualTo(" July ")
    }
}