package com.purkt.mindexpense.expense.presentation.screen.additem.state

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

class ExpenseAddInfoStateTest {
    @Test
    fun `When getLocalDateTime() is called, then return the proper LocalDateTime from input date string and time string`() {
        // Given
        val mockLocalDateTime = LocalDateTime.of(2022, Month.SEPTEMBER, 26, 23, 50)
        val dateFormatter = DateTimeFormatter.ofPattern(ExpenseAddInfoState.DATE_PATTERN)
        val timeFormatter = DateTimeFormatter.ofPattern(ExpenseAddInfoState.TIME_PATTERN)
        val mockAddState = ExpenseAddInfoState().apply {
            date = dateFormatter.format(mockLocalDateTime)
            time = timeFormatter.format(mockLocalDateTime)
        }

        // When
        val actual = mockAddState.getLocalDateTime()

        // Then
        assertEquals(mockLocalDateTime.year, actual.year)
        assertEquals(mockLocalDateTime.month, actual.month)
        assertEquals(mockLocalDateTime.dayOfMonth, actual.dayOfMonth)
        assertEquals(mockLocalDateTime.hour, actual.hour)
        assertEquals(mockLocalDateTime.minute, actual.minute)
    }
}